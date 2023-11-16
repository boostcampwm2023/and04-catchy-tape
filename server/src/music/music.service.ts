import { HttpException, Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { Music } from 'src/entity/music.entity';

@Injectable()
export class MusicService {
  //TODO: custom repository로 변경하기
  constructor(
    @InjectRepository(Music) private musicRepository: Repository<Music>,
  ) {}

  createMusic(musicCreateDto: MusicCreateDto, user_id: string) {
    try {
      const { title, cover, file: musicFile } = musicCreateDto;
      const newMusic: Music = this.musicRepository.create({
        title,
        cover,
        musicFile,
        created_at: new Date(),
        user_id,
      });

      this.musicRepository.save(newMusic);
    } catch {
      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
    }
  }
}
