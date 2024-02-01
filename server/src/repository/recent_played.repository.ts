import { Injectable, Logger } from '@nestjs/common';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { Music } from 'src/entity/music.entity';
import { Recent_Played } from 'src/entity/recent_played.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { DataSource, Repository } from 'typeorm';

@Injectable()
export class Recent_PlayedRepository {
  private recent_PlayedRepository: Repository<Recent_Played>;
  private readonly logger: Logger = new Logger('Recent_PlayedRepository');

  constructor(private readonly dataSource: DataSource) {
    this.recent_PlayedRepository = this.dataSource.getRepository(Recent_Played);
  }

  async getNumberOfRecentPlayedMusic(user_id: string): Promise<number> {
    return this.recent_PlayedRepository.count({
      where: { user: { user_id } },
    });
  }

  async countMusicNumberById(
    music_id: string,
    user_id: string,
  ): Promise<number> {
    return this.recent_PlayedRepository.count({
      where: { music: { music_id }, user: { user_id } },
    });
  }

  async getRecentPlayedId(music_id: string, user_id: string): Promise<number> {
    const recentPlayed = await this.recent_PlayedRepository.findOne({
      where: { music: { music_id }, user: { user_id } },
    });

    if (recentPlayed instanceof Recent_Played) {
      return recentPlayed.recent_played_id;
    }

    return 0;
  }

  async getRecentPlayedMusic(user_id: string): Promise<Recent_Played | null> {
    return this.recent_PlayedRepository.findOne({
      relations: { music: true, user: true },
      select: { music: { cover: true } },
      where: { user: { user_id } },
      order: { played_at: 'DESC' },
    });
  }

  async saveNewRecentMusic(music_id: string, user_id: string): Promise<number> {
    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      const newRow: Recent_Played = this.recent_PlayedRepository.create({
        music: { music_id },
        user: { user_id },
        played_at: new Date(),
      });

      const addedRow: Recent_Played = await queryRunner.manager.save(newRow);

      await queryRunner.commitTransaction();

      return addedRow.recent_played_id;
    } catch {
      await queryRunner.rollbackTransaction();

      this.logger.error(
        `recent_played.repository - saveNewRecentMusic : REPOSITORY_ERROR`,
      );
      throw new CatchyException(
        'REPOSITORY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.REPOSITORY_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }

  async getRecentPlayedMusicByUserId(
    user_id: string,
    count: number,
  ): Promise<Music[]> {
    return await this.recent_PlayedRepository
      .find({
        relations: {
          music: { user: true },
          user: true,
        },
        where: {
          user: { user_id },
        },
        select: {
          recent_played_id: false,
          music: {
            music_id: true,
            title: true,
            music_file: true,
            cover: true,
            genre: true,
            user: { user_id: true, nickname: true },
          },
        },
        order: {
          played_at: 'DESC',
        },
        take: count,
      })
      .then((recent_played: Recent_Played[]) =>
        recent_played.map((recent) => recent.music),
      );
  }
}
