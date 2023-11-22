import {
  Column,
  Entity,
  CreateDateColumn,
  BaseEntity,
  JoinColumn,
  PrimaryGeneratedColumn,
  ManyToOne,
  OneToMany,
} from 'typeorm';
import { User } from './user.entity';
import { Genres } from 'src/constants';
import { Music_Playlist } from './music_playlist.entity';

@Entity({ name: 'music' })
export class Music extends BaseEntity {
  @PrimaryGeneratedColumn()
  musicId: number;

  @Column()
  title: string;

  @Column({ nullable: true })
  lyrics: string | null;

  @Column()
  cover: string;

  @Column()
  musicFile: string;

  @Column()
  genre: Genres;

  @CreateDateColumn()
  created_at: Date;

  @ManyToOne(() => User, (user) => user.musics)
  @JoinColumn({ name: 'user_id' })
  user: User;

  @OneToMany(() => Music_Playlist, (music_playlist) => music_playlist.music)
  music_playlist: Music_Playlist[];
}
