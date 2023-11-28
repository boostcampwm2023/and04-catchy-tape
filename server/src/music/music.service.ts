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

@Injectable()
export class MusicService {
  constructor(
    @InjectRepository(Music) private musicRepository: Repository<Music>,
    private uploadService: UploadService,
  ) {}

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

      await this.executeEncoding(
        tempFilePath,
        outputPath,
        outputMusicPath,
        musicId,
      );

      const encodedFileURL = await this.uploadEncodedFiles(
        outputMusicPath,
        musicId,
      );

      fs.rmdirSync(entireMusicPath, { recursive: true });

      return encodedFileURL;
    } catch(err) {
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
    return await new Promise((resolve, reject) => {
      ffmpeg(tempFilePath)
        .addOption([
          '-c:a aac',
          '-b:a 192k',
          '-hls_time 10',
          '-hls_list_size 0',
          '-f hls',
        ])
        .output(outputPath)
        .on('end', async () => {
          const encodedPath = await this.uploadEncodedFiles(
            outputMusicPath,
            musicId,
          );
          resolve(encodedPath);
        })
        .on('error', () => {
          reject(new Error());
        })
        .run();
    });
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
        'MUSIC_ENCODE_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVER_ERROR,
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

      const encodedFileURL = await this.encodeMusic(
        music_id,
        music_file.split('?')[0],
      );

      const newMusic: Music = this.musicRepository.create({
        music_id,
        title,
        cover: cover.split('?')[0],
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
}
