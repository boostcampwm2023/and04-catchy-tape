import {
  BaseEntity,
  Column,
  Entity,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Music } from './music.entity';
import { Playlist } from './playlist.entity';
import { RECENT_PLAYLIST_NAME } from 'src/constants';

@Entity({ name: 'music_playlist' })
export class Music_Playlist extends BaseEntity {
  @PrimaryGeneratedColumn()
  music_playlist_id: number;

  @ManyToOne(() => Music, (music) => music.music_playlist)
  @JoinColumn({ name: 'music_id' })
  music: Music;

  @ManyToOne(() => Playlist, (playlist) => playlist.music_playlist)
  @JoinColumn({ name: 'playlist_id' })
  playlist: Playlist;

  @Column()
  created_at: Date;

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

  static async getRecentPlayedMusicByUserId(userId: string): Promise<Music[]> {
    return await this.find({
      relations: {
        music: true,
      },
      where: {
        playlist: {
          playlist_title: RECENT_PLAYLIST_NAME,
        },
        music: {
          user: {
            user_id: userId,
          },
        },
      },
      select: {
        music_playlist_id: false,
        music: {
          music_id: true,
          title: true,
          music_file: true,
          cover: true,
          genre: true,
        },
      },
      order: {
        created_at: 'DESC',
      },
      take: 10,
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
}
