import { Injectable, Logger } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { Repository, DataSource } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { Music } from 'src/entity/music.entity';
import { Genres, SLICE_COUNT } from 'src/constants';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { UploadService } from 'src/upload/upload.service';
import { NcloudConfigService } from 'src/config/ncloud.config';
import { AWSError } from 'aws-sdk';
import { DeleteObjectOutput } from 'aws-sdk/clients/s3';

@Injectable()
export class MusicService {
  private readonly logger = new Logger('MusicService');
  private objectStorage: AWS.S3;
  constructor(
    @InjectRepository(Music) private musicRepository: Repository<Music>,
    private uploadService: UploadService,
    private readonly ncloudConfigService: NcloudConfigService,
    private readonly dataSource: DataSource,
  ) {
    this.objectStorage = ncloudConfigService.createObjectStorageOption();
  }

  private isValidGenre(genre: string): boolean {
    if (Object.values(Genres).includes(genre as Genres)) {
      return true;
    }

    return false;
  }

  async createMusic(
    musicCreateDto: MusicCreateDto,
    user_id: string,
  ): Promise<string> {
    const { music_id, title, cover, file: music_file, genre } = musicCreateDto;

    if (!this.isValidGenre(genre)) {
      this.logger.error(`music.service - createMusic : NOT_EXIST_GENRE`);
      throw new CatchyException(
        'NOT_EXIST_GENRE',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.NOT_EXIST_GENRE,
      );
    }

    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      const newMusic: Music = this.musicRepository.create({
        music_id,
        title,
        cover,
        music_file,
        created_at: new Date(),
        genre,
        user: { user_id },
      });

      await queryRunner.manager.save(newMusic);

      await queryRunner.commitTransaction();

      return music_id;
    } catch (err) {
      await queryRunner.rollbackTransaction();

      if (err instanceof CatchyException) {
        throw err;
      }

      this.logger.error(`music.service - createMusic : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }

  async getRecentMusic(): Promise<Music[]> {
    try {
      return Music.getRecentMusic();
    } catch {
      this.logger.error(`music.service - getRecentMusic : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getMyUploads(userId: string, count: number): Promise<Music[]> {
    try {
      return Music.getMusicListByUserId(userId, count);
    } catch {
      this.logger.error(`music.service - getMyUploads : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getMusicInfo(music_id: string): Promise<Music> {
    try {
      const targetMusic: Music = await Music.getMusicById(music_id);

      if (!targetMusic) {
        this.logger.error(`music.service - getMusicInfo : NOT_EXIST_MUSIC`);
        throw new CatchyException(
          'NOT_EXIST_MUSIC',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.NOT_EXIST_MUSIC,
        );
      }
      return targetMusic;
    } catch (err) {
      if (err instanceof CatchyException) throw err;

      this.logger.error(`music.service - getMusicInfo : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getEncodedChunkFiles(
    musicId: string,
    fileName: string,
  ): Promise<AWS.S3.Body> {
    try {
      const filePath = `music/${musicId}/${fileName}`;

      const result = await this.objectStorage
        .getObject({
          Bucket: 'catchy-tape-bucket2',
          Key: filePath,
        })
        .promise();

      return result.Body;
    } catch (err) {
      const awsError = err as AWSError;

      if (awsError && awsError.code === 'NoSuchKey') {
        this.logger.error(
          `music.service - getEncodedChunkFiles : NOT_EXIST_TS_IN_BUCKET`,
        );
        throw new CatchyException(
          'NOT_EXIST_TS_IN_BUCKET',
          HTTP_STATUS_CODE.NOT_FOUND,
          ERROR_CODE.NOT_EXIST_TS_IN_BUCKET,
        );
      }

      this.logger.error(`music.service - getEncodedChunkFiles : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getCertainKeywordNicknameUser(keyword: string): Promise<Music[]> {
    try {
      return await Music.getCertainMusicByTitle(keyword);
    } catch {
      this.logger.error(
        `music.service - getCertainKeywordNicknameUser : QUERY_ERROR`,
      );
      throw new CatchyException(
        'QUERY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.QUERY_ERROR,
      );
    }
  }

  async deleteMusicById(musicId: string, userId: string): Promise<string> {
    if (!(await Music.isMusicOwner(musicId, userId))) {
      this.logger.error(`music.service - deleteMusicById : NOT_EXIST_MUSIC`);
      throw new CatchyException(
        'NOT_EXIST_MUSIC',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.NOT_EXIST_MUSIC,
      );
    }

    try {
      const music: Music = await Music.getMusicById(musicId);
      this.musicRepository.delete(music.music_id);

      const musicFilePath: string = music.music_file.slice(SLICE_COUNT, SLICE_COUNT + 41);
      const coverFilePath: string = music.cover.slice(SLICE_COUNT, SLICE_COUNT + 46);

      if (musicFilePath) this.deleteFolder(musicFilePath);
      if (coverFilePath) this.deleteFolder(coverFilePath);

      return music.music_id;
    } catch {
      this.logger.error(`music.service - deleteMusicById : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVICE_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async deleteFolder(folderPath) {
    let params = {
      Bucket: 'catchy-tape-bucket2',
      Delete: {
        Objects: [],
      },
    };

    // 폴더 내의 파일들을 삭제할 객체 목록에 추가
    const filesInFolder = await this.listFilesInFolder(folderPath);
    filesInFolder.forEach((file) => {
      params.Delete.Objects.push({ Key: file.Key });
    });

    try {
      // 폴더 내의 파일들을 삭제
      await this.objectStorage.deleteObjects(params).promise();

      // 폴더를 삭제
      await this.objectStorage
        .deleteObject({ Bucket: 'catchy-tape-bucket2', Key: folderPath })
        .promise();
    } catch (error) {
      this.logger.error(`music.service - deleteFolder : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVICE_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async listFilesInFolder(folderPath) {
    let params = {
      Bucket: 'catchy-tape-bucket2',
      Prefix: folderPath,
    };

    try {
      const data = await this.objectStorage.listObjectsV2(params).promise();
      return data.Contents;
    } catch (error) {
      this.logger.error(`music.service - listFilesInFolder : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVICE_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }
}
