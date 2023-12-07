import {
  BaseEntity,
  Column,
  Entity,
  Index,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Music } from './music.entity';
import { Playlist } from './playlist.entity';

@Entity({ name: 'music_playlist' })
export class Music_Playlist extends BaseEntity {
  @PrimaryGeneratedColumn()
  music_playlist_id: number;

  @ManyToOne(() => Music, (music) => music.music_playlist)
  @JoinColumn({ name: 'music_id' })
  music: Music;

  @ManyToOne(() => Playlist, (playlist) => playlist.music_playlist)
  @JoinColumn({ name: 'playlist_id' })
  @Index()
  playlist: Playlist;

  @Column()
  @Index()
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
