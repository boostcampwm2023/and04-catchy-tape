import { BaseEntity, Entity, JoinColumn, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import { Music } from './music.entity';
import { Playlist } from './playlist.entity';

@Entity({ name: 'music_playlist' })
export class Music_Playlist extends BaseEntity {
  @PrimaryGeneratedColumn()
  music_playlist_id: number;

  @ManyToOne(() => Music, (music) => music.music_playlist)
  @JoinColumn({name: 'music_id'})
  music: Music

  @ManyToOne(() => Playlist, (playlist) => playlist.music_playlist)
  @JoinColumn({name: 'playlist_id'})
  playlist: Playlist
}
