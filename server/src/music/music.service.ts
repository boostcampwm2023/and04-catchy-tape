import { Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { Music } from 'src/entity/music.entity';
import { Genres } from 'src/constants';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';

@Injectable()
export class MusicService {
  //TODO: custom repository로 변경하기
  constructor(
    @InjectRepository(Music) private musicRepository: Repository<Music>,
  ) {}

  isValidGenre(genre: string): boolean {
    if (Object.values(Genres).includes(genre as Genres)) {
      return true;
    }

    return false;
  }

  async createMusic(
    musicCreateDto: MusicCreateDto,
    user_id: string,
  ): Promise<number> {
    try {
      const { title, cover, file: musicFile, genre } = musicCreateDto;

      if (!this.isValidGenre(genre)) {
        throw new CatchyException(
          'NOT_EXIST_GENRE',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.NOT_EXIST_GENRE,
        );
      }

      const newMusic: Music = this.musicRepository.create({
        title,
        cover,
        musicFile,
        created_at: new Date(),
        genre,
        user: { user_id: user_id },
      });

      const savedMusic: Music = await this.musicRepository.save(newMusic);
      return savedMusic.musicId;
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
      return await this.musicRepository.find({
        relations: {
          user: true,
        },
        select: {
          musicId: true,
          title: true,
          lyrics: true,
          cover: true,
          musicFile: true,
          genre: true,
          created_at: true,
          user: {
            user_id: true,
            nickname: true,
          },
        },
        order: {
          created_at: 'DESC',
        },
        take: 10,
      });
    } catch {
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }
}
