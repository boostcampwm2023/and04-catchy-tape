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
import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { CatchyException } from 'src/config/catchyException';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { Logger } from '@nestjs/common';

@Entity({ name: 'music' })
export class Music extends BaseEntity {
  private static readonly logger: Logger = new Logger('MusicEntity');

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
