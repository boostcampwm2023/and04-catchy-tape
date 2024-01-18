import {
  BaseEntity,
  Column,
  CreateDateColumn,
  Entity,
  EntityManager,
  Index,
  JoinColumn,
  ManyToOne,
  OneToMany,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { User } from './user.entity';
import { Music_Playlist } from './music_playlist.entity';
import { PlaylistCreateDto } from 'src/dto/playlistCreate.dto';
import { CatchyException } from 'src/config/catchyException';
import { Logger } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { ERROR_CODE } from 'src/config/errorCode.enum';

@Entity({ name: 'playlist' })
export class Playlist extends BaseEntity {
  private static readonly logger: Logger = new Logger('PlaylistEntity');

  @PrimaryGeneratedColumn()
  playlist_id: number;

  @Column()
  playlist_title: string;

  @CreateDateColumn()
  created_at: Date;

  @Column()
  updated_at: Date;

  @ManyToOne(() => User, (user) => user.playlists)
  @JoinColumn({ name: 'user_id' })
  @Index()
  user: User;

  @OneToMany(() => Music_Playlist, (music_playlist) => music_playlist.playlist)
  music_playlist: Music_Playlist[];

  static async isExistPlaylistOnUser(
    userId: string,
    playlistId: number,
  ): Promise<number> {
    return this.countBy({
      playlist_id: playlistId,
      user: { user_id: userId },
    });
  }

  static async getPlaylistsByUserId(userId: string): Promise<Playlist[]> {
    return await this.find({
      select: { playlist_id: true, playlist_title: true },
      where: {
        user: { user_id: userId },
      },
      order: {
        updated_at: 'DESC',
      },
    });
  }

  static async createPlaylist(
    userId: string,
    playlistCreateDto: PlaylistCreateDto,
  ): Promise<number> {
    const entityManager: EntityManager = this.getRepository().manager;
    const queryRunner = entityManager.connection.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      const title: string = playlistCreateDto.title;
      const newPlaylist: Playlist = this.create({
        playlist_title: title,
        created_at: new Date(),
        updated_at: new Date(),
        user: { user_id: userId },
      });

      const result: Playlist = await queryRunner.manager.save(newPlaylist);
      await queryRunner.commitTransaction();

      const playlistId: number = result.playlist_id;

      return playlistId;
    } catch {
      await queryRunner.rollbackTransaction();

      this.logger.error(`playlist.entity - createPlaylist : ENTITY_ERROR`);
      throw new CatchyException(
        'ENTITY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.ENTITY_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }

  static async
}
