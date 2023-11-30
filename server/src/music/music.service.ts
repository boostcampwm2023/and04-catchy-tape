import { Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { Music } from 'src/entity/music.entity';
import { Genres } from 'src/constants';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import * as fs from 'fs';
import * as ffmpeg from 'fluent-ffmpeg';
import * as ffmpegInstaller from '@ffmpeg-installer/ffmpeg';
import * as path from 'path';
import axios from 'axios';
import { UploadService } from 'src/upload/upload.service';
import { NcloudConfigService } from 'src/config/ncloud.config';
import { AWSError } from 'aws-sdk';

@Injectable()
export class MusicService {
  private objectStorage: AWS.S3;
  constructor(
    @InjectRepository(Music) private musicRepository: Repository<Music>,
    private uploadService: UploadService,
    private readonly ncloudConfigService: NcloudConfigService,
  ) {
    this.objectStorage = ncloudConfigService.createObjectStorageOption();
  }

  isValidGenre(genre: string): boolean {
    if (Object.values(Genres).includes(genre as Genres)) {
      return true;
    }

    return false;
  }

  separateMusicName(musicPath: string): string {
    const parsedPath = new URL(musicPath);
    const pathNames = parsedPath.pathname.split('/');
    const musicName = pathNames[pathNames.length - 1];

    return musicName;
  }

  getPath(option: string): string {
    return path.resolve(__dirname, `musics${option}`);
  }

  setEncodingPaths(musicPath: string) {
    const musicName: string = this.separateMusicName(musicPath);

    return {
      outputMusicPath: this.getPath('/output'),
      entireMusicPath: this.getPath(''),
      outputPath: this.getPath(`/output/${musicName.replace('.mp3', '')}.m3u8`),
      tempFilePath: this.getPath(`/${musicName}`),
    };
  }

  async encodeMusic(musicId: string, musicPath: string): Promise<string> {
    try {
      ffmpeg.setFfmpegPath(ffmpegInstaller.path);

      const { outputMusicPath, entireMusicPath, outputPath, tempFilePath } =
        this.setEncodingPaths(musicPath);

      fs.mkdirSync(outputMusicPath, { recursive: true });

      const musicFileResponse = await axios.get(musicPath, {
        responseType: 'arraybuffer',
      });

      const musicBuffer = Buffer.from(musicFileResponse.data);

      fs.writeFile(tempFilePath, musicBuffer, (err) => {
        if (err) throw new Error();
      });

      const encodedFileURL = await this.executeEncoding(
        tempFilePath,
        outputPath,
        outputMusicPath,
        musicId,
      );

      fs.rmdirSync(entireMusicPath, { recursive: true });

      return encodedFileURL;
    } catch (err) {
      if (err instanceof CatchyException) {
        throw err;
      }
      console.log(err);

      throw new CatchyException(
        'MUSIC_ENCODE_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVER_ERROR,
      );
    }
  }

  async executeEncoding(
    tempFilePath: string,
    outputPath: string,
    outputMusicPath: string,
    musicId: string,
  ): Promise<string> {
    let m3u8FileName;
    let m3u8Path: string;
    const watcher = fs.watch(outputMusicPath, (eventType, fileName) => {
      if (fileName.match(/.m3u8$/)) {
        m3u8FileName = fileName;
      } else if (!fileName.match(/\.tmp$/)) {
        this.uploadEncodedFile(
          outputMusicPath + `/${fileName}`,
          musicId,
          fileName,
        );
      }
    });
    return await new Promise<string>((resolve, reject) => {
      ffmpeg(tempFilePath)
        .addOption([
          '-map 0:a',
          '-c:a aac',
          '-b:a 192k',
          '-hls_time 30',
          '-hls_list_size 0',
          '-f hls',
        ])
        .output(outputPath)
        .on('end', async () => {
          watcher.close();
          m3u8Path = await this.uploadEncodedFile(
            outputMusicPath + `/${m3u8FileName}`,
            musicId,
            m3u8FileName,
          );
          resolve(m3u8Path);
        })
        .on('error', () => {
          reject(new Error());
        })
        .run();
    });
  }

  async uploadEncodedFile(
    file: string,
    musicId: string,
    fileName: string,
  ): Promise<string> {
    const { url } = await this.uploadService.uploadEncodedFile(
      file,
      musicId,
      fileName,
    );
    return url;
  }

  async uploadEncodedFiles(
    folderPath: string,
    musicId: string,
  ): Promise<string> {
    try {
      let m3u8Path = '';

      const files = await fs.promises.readdir(folderPath);

      for (const file of files) {
        const sourceFilePath = path.join(folderPath, file);

        const storagePath = await this.uploadService.uploadEncodedFile(
          sourceFilePath,
          musicId,
          file,
        );

        if (file.includes('.m3u8')) m3u8Path = storagePath.url;
      }

      return m3u8Path;
    } catch {
      throw new CatchyException(
        'ENCODED_MUSIC_UPLOAD_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.ENCODED_MUSIC_UPLOAD_ERROR,
      );
    }
  }

  async createMusic(
    musicCreateDto: MusicCreateDto,
    user_id: string,
  ): Promise<string> {
    try {
      const {
        music_id,
        title,
        cover,
        file: music_file,
        genre,
      } = musicCreateDto;

      if (!this.isValidGenre(genre)) {
        throw new CatchyException(
          'NOT_EXIST_GENRE',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.NOT_EXIST_GENRE,
        );
      }

      const encodedFileURL = await this.encodeMusic(music_id, music_file);

      const newMusic: Music = this.musicRepository.create({
        music_id,
        title,
        cover,
        music_file: encodedFileURL,
        created_at: new Date(),
        genre,
        user: { user_id },
      });

      const savedMusic: Music = await this.musicRepository.save(newMusic);

      return savedMusic.music_id;
    } catch (err) {
      if (err instanceof CatchyException) {
        throw err;
      }

      console.log(err);
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getRecentMusic(): Promise<Music[]> {
    try {
      return Music.getRecentMusic();
    } catch {
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
        throw new CatchyException(
          'NOT_EXIST_MUSIC',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.NOT_EXIST_MUSIC,
        );
      }
      return targetMusic;
    } catch (err) {
      if (err instanceof CatchyException) throw err;
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
        throw new CatchyException(
          'NOT_EXIST_TS_IN_BUCKET',
          HTTP_STATUS_CODE.NOT_FOUND,
          ERROR_CODE.NOT_EXIST_TS_IN_BUCKET,
        );
      }

      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }
}
