import {
  BaseEntity,
  Column,
  CreateDateColumn,
  Entity,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { User } from './user.entity';

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

  @ManyToOne(
    () => User,
    (user) => {
      user.playlists;
    },
  )
  @JoinColumn({ name: 'user_id' })
  user: User;
}
