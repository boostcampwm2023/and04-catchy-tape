import {
  BaseEntity,
  Column,
  CreateDateColumn,
  Entity,
  JoinColumn,
  ManyToOne,
  OneToMany,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { User } from './user.entity';
import { Music_Playlist } from './music_playlist.entity';

@Entity({ name: 'playlist' })
export class Playlist extends BaseEntity {
  @PrimaryGeneratedColumn()
  playlistId: string;

  @Column()
  playlist_title: string;

  @CreateDateColumn()
  created_at: Date;

  @Column()
  updated_at: Date;

  @ManyToOne(() => User, (user) => user.playlists)
  @JoinColumn({ name: 'user_id' })
  user: User;

  @OneToMany(() => Music_Playlist, (music_playlist) => music_playlist.playlist)
  music_playlist: Music_Playlist[];
}
