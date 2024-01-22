import {
  Column,
  Entity,
  CreateDateColumn,
  BaseEntity,
  JoinColumn,
  ManyToOne,
  OneToMany,
  PrimaryColumn,
  ILike,
  Index,
} from 'typeorm';
import { User } from './user.entity';
import { Genres } from 'src/constants';
import { Music_Playlist } from './music_playlist.entity';
import { Recent_Played } from './recent_played.entity';
import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { CatchyException } from 'src/config/catchyException';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { Logger } from '@nestjs/common';

@Entity({ name: 'music' })
export class Music extends BaseEntity {
  private static readonly logger: Logger = new Logger('MusicEntity');

  @PrimaryColumn()
  music_id: string;

  @Column()
  title: string;

  @Column({ nullable: true })
  lyrics: string | null;

  @Column()
  cover: string;

  @Column()
  music_file: string;

  @Column()
  genre: Genres;

  @CreateDateColumn()
  @Index()
  created_at: Date;

  @ManyToOne(() => User, (user) => user.musics)
  @JoinColumn({ name: 'user_id' })
  user: User;

  @OneToMany(() => Music_Playlist, (music_playlist) => music_playlist.music)
  music_playlist: Music_Playlist[];

  @OneToMany(() => Recent_Played, (recent_played) => recent_played.music)
  recent_played: Recent_Played[];

  static async getMusicListByUserId(
    userId: string,
    count: number,
  ): Promise<Music[]> {
    return this.find({
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

  static async countMusicById(musicId: string): Promise<number> {
    return this.countBy({ music_id: musicId });
  }

  static async getRecentMusic(): Promise<Music[]> {
    return this.find({
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

  static async getMusicById(music_id: string): Promise<Music> {
    return this.findOne({
      relations: { user: true },
      select: { user: { user_id: true, nickname: true } },
      where: { music_id },
    });
  }

  static async getCertainMusicByTitle(keyword: string): Promise<Music[]> {
    return await this.find({
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

  static async isExistMusicId(musicId: string): Promise<boolean> {
    const count: number = await this.count({ where: { music_id: musicId } });
    if (count === 0) {
      return false;
    }
    return true;
  }

  static async isMusicOwner(musicId: string, userId: string): Promise<boolean> {
    const count: number = await this.count({
      where: { music_id: musicId, user: { user_id: userId } },
    });
    if (count === 0) {
      return false;
    }
    return true;
  }

  static async saveMusic(
    musicCreateDto: MusicCreateDto,
    user_id: string,
  ): Promise<void> {
    const { music_id, title, cover, file: music_file, genre } = musicCreateDto;

    const entityManager = this.getRepository().manager;
    const queryRunner = entityManager.connection.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      const newMusic: Music = this.create({
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
    } catch {
      await queryRunner.rollbackTransaction();

      this.logger.error(`music.entity - saveMusic : ENTITY_ERROR`);
      throw new CatchyException(
        'ENTITY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.ENTITY_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }

  static async deleteMusic(music: Music): Promise<void> {
    const entityManager = this.getRepository().manager;
    const queryRunner = entityManager.connection.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      await this.remove(music);

      await queryRunner.commitTransaction();
    } catch {
      await queryRunner.rollbackTransaction();

      this.logger.error(`music.entity - deleteMusic : ENTITY_ERROR`);
      throw new CatchyException(
        'ENTITY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.ENTITY_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }
}
