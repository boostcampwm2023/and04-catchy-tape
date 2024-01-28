import {
  Column,
  Entity,
  CreateDateColumn,
  BaseEntity,
  JoinColumn,
  ManyToOne,
  OneToMany,
  PrimaryColumn,
  Index,
} from 'typeorm';
import { User } from './user.entity';
import { Genres } from 'src/constants';
import { Music_Playlist } from './music_playlist.entity';
import { Recent_Played } from './recent_played.entity';

@Entity({ name: 'music' })
export class Music extends BaseEntity {
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
}
