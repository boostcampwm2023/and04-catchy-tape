import { Injectable, Logger } from '@nestjs/common';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { Music } from 'src/entity/music.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { DataSource, ILike, Repository } from 'typeorm';

@Injectable()
export class MusicRepository {
  private musicRepository: Repository<Music>;
  private readonly logger: Logger = new Logger('MusicRepository');

  constructor(private readonly dataSource: DataSource) {
    this.musicRepository = this.dataSource.getRepository(Music);
  }

  async getMusicListByUserId(userId: string, count: number): Promise<Music[]> {
    return this.musicRepository.find({
      relations: {
        user: true,
      },
      where: {
        user: { user_id: userId },
      },
      select: {
        music_id: true,
        title: true,
        lyrics: true,
        cover: true,
        music_file: true,
        genre: true,
        created_at: true,
        user: { user_id: true, nickname: true },
      },
      order: {
        created_at: 'DESC',
      },
      take: count,
    });
  }

  async countMusicById(musicId: string): Promise<number> {
    return this.musicRepository.countBy({ music_id: musicId });
  }

  async getRecentMusic(): Promise<Music[]> {
    return this.musicRepository.find({
      relations: {
        user: true,
      },
      select: {
        music_id: true,
        title: true,
        lyrics: true,
        cover: true,
        music_file: true,
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
  }

  async getMusicById(music_id: string): Promise<Music | null> {
    return this.musicRepository.findOne({
      relations: { user: true },
      select: { user: { user_id: true, nickname: true } },
      where: { music_id },
    });
  }

  async getCertainMusicByTitle(keyword: string): Promise<Music[]> {
    return await this.musicRepository.find({
      relations: {
        user: true,
        music_playlist: false,
      },
      select: {
        music_id: true,
        lyrics: true,
        title: true,
        cover: true,
        music_file: true,
        genre: true,
        user: {
          user_id: true,
          nickname: true,
        },
      },
      where: {
        title: ILike(`%${keyword}%`),
      },
      order: {
        created_at: 'DESC',
      },
    });
  }

  async isExistMusicId(musicId: string): Promise<boolean> {
    const count: number = await this.musicRepository.count({
      where: { music_id: musicId },
    });
    if (count === 0) {
      return false;
    }
    return true;
  }

  async isMusicOwner(musicId: string, userId: string): Promise<boolean> {
    const count: number = await this.musicRepository.count({
      where: { music_id: musicId, user: { user_id: userId } },
    });
    if (count === 0) {
      return false;
    }
    return true;
  }

  async addMusic(
    musicCreateDto: MusicCreateDto,
    user_id: string,
  ): Promise<string> {
    const { music_id, title, cover, file: music_file, genre } = musicCreateDto;

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

      this.logger.error(`music.repository - addMusic : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }
}
