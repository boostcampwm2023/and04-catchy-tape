import { HttpException, Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { Music } from 'src/entity/music.entity';
import { Genres } from 'src/constants';

@Injectable()
export class MusicService {
  //TODO: custom repository로 변경하기
  constructor(
    @InjectRepository(Music) private musicRepository: Repository<Music>,
  ) {}

  isValidGenre(genre: string) {
    if (Object.values(Genres).includes(genre as Genres)) {
      return true;
    }

    return false;
  }

  createMusic(musicCreateDto: MusicCreateDto, user_id: string) {
    try {
      const { title, cover, file: musicFile, genre } = musicCreateDto;

      if (!this.isValidGenre(genre)) {
        throw new HttpException(
          'NOT_EXIST_GENRE',
          HTTP_STATUS_CODE.NOT_EXIST_GENRE,
        );
      }

      const newMusic: Music = this.musicRepository.create({
        title,
        cover,
        musicFile,
        created_at: new Date(),
        genre,
        user_id,
      });

      this.musicRepository.save(newMusic);
    } catch (err) {
      if (err instanceof HttpException) {
        throw err;
      }

      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
    }
  }

  async getRecentMusic() {
    try {
      const musics = await this.musicRepository.find({
        order: {
          created_at: 'DESC',
        },
        take: 10,
      });

      return musics;
    } catch {
      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
    }
  }
}
