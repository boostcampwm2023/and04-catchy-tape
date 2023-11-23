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
  playlist_Id: number;

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

  static async getPlaylistsByUserId(userId: string): Promise<Playlist[]> {
    return this.find({
      select: { playlist_Id: true, playlist_title: true },
      where: {
        user: { user_id: userId },
      },
      order: {
        updated_at: 'DESC',
      },
    });
  }
}
