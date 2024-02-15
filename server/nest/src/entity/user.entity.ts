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
import { Recent_Played } from './recent_played.entity';
import { Logger } from '@nestjs/common';

@Entity({ name: 'user' })
export class User extends BaseEntity {
  private static readonly logger: Logger = new Logger('UserEntity');

  @PrimaryColumn()
  user_id: string;

  @Column()
  nickname: string;

  @Column({ nullable: true })
  photo?: string;

  @Column()
  user_email: string;

  @CreateDateColumn()
  created_at: Date;

  @Column({ default: false })
  is_deleted: boolean;

  @OneToMany(() => Music, (music) => music.user)
  musics: Music[];

  @OneToMany(() => Playlist, (playlist) => playlist.user)
  playlists: Playlist[];

  @OneToMany(() => Recent_Played, (recent_played) => recent_played.user)
  recent_played: Recent_Played[];
}
