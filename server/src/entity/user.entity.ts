import {
  Column,
  Entity,
  CreateDateColumn,
  BaseEntity,
  PrimaryColumn,
  OneToMany,
} from 'typeorm';
import { Playlist } from './playlist.entity';
import { Music } from './music.entity';

@Entity({ name: 'user' })
export class User extends BaseEntity {
  @PrimaryColumn()
  user_id: string;

  @Column()
  nickname: string;

  @Column({ nullable: true })
  photo: string | null;

  @Column()
  user_email: string;

  @CreateDateColumn()
  created_at: Date;

  @OneToMany(() => Music, (music) => music.user)
  musics: Music[];

  @OneToMany(() => Playlist, (playlist) => playlist.user)
  playlists: Playlist[];
}
