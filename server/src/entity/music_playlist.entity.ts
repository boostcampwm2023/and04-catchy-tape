import {
  BaseEntity,
  Entity,
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
  playlist: Playlist;

  static async getMusicListByPlaylistId(playlistId: number): Promise<Music[]> {
    return this.find({
      relations: {
        music: { user: true },
      },
      where: {
        playlist: { playlist_Id: playlistId },
      },
      select: {
        music: {
          musicId: true,
          title: true,
          cover: true,
          musicFile: true,
          genre: true,
          user: { user_id: true, nickname: true },
        },
        music_playlist_id: false,
      },
      order: {
        music_playlist_id: 'DESC',
      },
    }).then((a: Music_Playlist[]) => a.map((b) => b.music));
  }
}
