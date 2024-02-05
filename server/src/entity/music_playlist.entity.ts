import {
  BaseEntity,
  Column,
  Entity,
  Index,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Music } from './music.entity';
import { Playlist } from './playlist.entity';
import { Logger } from '@nestjs/common';

@Entity({ name: 'music_playlist' })
export class Music_Playlist extends BaseEntity {
  private static readonly logger: Logger = new Logger('MusicPlaylistEntity');

  @PrimaryGeneratedColumn()
  music_playlist_id: number;

  @ManyToOne(() => Music, (music) => music.music_playlist, {
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'music_id' })
  music: Music;

  @ManyToOne(() => Playlist, (playlist) => playlist.music_playlist)
  @JoinColumn({ name: 'playlist_id' })
  @Index()
  playlist: Playlist;

  @Column()
  @Index()
  created_at: Date;
}
