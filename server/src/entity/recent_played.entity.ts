import {
  BaseEntity,
  Column,
  Entity,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Music } from './music.entity';
import { User } from './user.entity';

@Entity({ name: 'recent_played' })
export class Recent_Played extends BaseEntity {
  @PrimaryGeneratedColumn()
  recent_played_id: number;

  @ManyToOne(() => Music, (music) => music.recent_played)
  @JoinColumn({ name: 'music_id' })
  music: Music;

  @ManyToOne(() => User, (user) => user.recent_played)
  @JoinColumn({ name: 'user_id' })
  user: User;

  @Column()
  played_at: Date;
}
