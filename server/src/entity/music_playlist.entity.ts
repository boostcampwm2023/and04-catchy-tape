import {
  BaseEntity,
  Column,
  Entity,
  EntityManager,
  Index,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Music } from './music.entity';
import { Playlist } from './playlist.entity';
import { Logger } from '@nestjs/common';
import { CatchyException } from 'src/config/catchyException';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { ERROR_CODE } from 'src/config/errorCode.enum';

@Entity({ name: 'music_playlist' })
export class Music_Playlist extends BaseEntity {
  private static readonly logger: Logger = new Logger('MusicPlaylistEntity');

  @PrimaryGeneratedColumn()
  music_playlist_id: number;

  @ManyToOne(() => Music, (music) => music.music_playlist, {
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'music_id' })
  music: Music;

  @ManyToOne(() => Playlist, (playlist) => playlist.music_playlist)
  @JoinColumn({ name: 'playlist_id' })
  @Index()
  playlist: Playlist;

  @Column()
  @Index()
  created_at: Date;

  static async countMusicNumberInPlaylist(
    musicId: string,
    playlistId: number,
  ): Promise<number> {
    return this.countBy({
      music: { music_id: musicId },
      playlist: { playlist_id: playlistId },
    });
  }

  static async getMusicListByPlaylistId(playlistId: number): Promise<Music[]> {
    return this.find({
      relations: {
        music: { user: true },
      },
      where: {
        playlist: { playlist_id: playlistId },
      },
      select: {
        music: {
          music_id: true,
          title: true,
          cover: true,
          music_file: true,
          genre: true,
          user: { user_id: true, nickname: true },
        },
        music_playlist_id: false,
      },
      order: {
        created_at: 'DESC',
      },
    }).then((a: Music_Playlist[]) => a.map((b) => b.music));
  }

  static async getMusicCountByPlaylistId(playlist_id: number): Promise<number> {
    return this.count({ where: { playlist: { playlist_id } } });
  }

  static async getThumbnailByPlaylistId(
    playlist_id: number,
  ): Promise<Music_Playlist> {
    return this.findOne({
      relations: { music: true },
      select: { music: { cover: true } },
      where: { playlist: { playlist_id } },
      order: { created_at: 'DESC' },
    });
  }

  static async addMusicToPlaylist(
    musicId: string,
    playlistId: number,
  ): Promise<number> {
    const entityManager: EntityManager = this.getRepository().manager;
    const queryRunner = entityManager.connection.createQueryRunner();
    await queryRunner.startTransaction();

    // 관계테이블에 추가
    try {
      const new_music_playlist: Music_Playlist = this.create({
        music: { music_id: musicId },
        playlist: { playlist_id: playlistId },
        created_at: new Date(),
      });

      await queryRunner.manager.save(new_music_playlist);
      await queryRunner.manager.update(
        Playlist,
        { playlist_id: playlistId },
        { updated_at: new Date() },
      );

      await queryRunner.commitTransaction();

      return new_music_playlist.music_playlist_id;
    } catch {
      await queryRunner.rollbackTransaction();

      this.logger.error(
        `playlist.entity - addMusicToPlaylist : ENTITY_ERROR`,
      );
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
