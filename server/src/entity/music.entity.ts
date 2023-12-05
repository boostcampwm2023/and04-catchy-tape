import {
  Column,
  Entity,
  CreateDateColumn,
  BaseEntity,
  JoinColumn,
  ManyToOne,
  OneToMany,
  PrimaryColumn,
  ILike,
} from 'typeorm';
import { User } from './user.entity';
import { Genres } from 'src/constants';
import { Music_Playlist } from './music_playlist.entity';

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
  created_at: Date;

  @ManyToOne(() => User, (user) => user.musics)
  @JoinColumn({ name: 'user_id' })
  user: User;

  @OneToMany(() => Music_Playlist, (music_playlist) => music_playlist.music)
  music_playlist: Music_Playlist[];

  static async getMusicListByUserId(
    userId: string,
    count: number,
  ): Promise<Music[]> {
    return this.find({
      relations: {
        user: true,
      },
      where: {
        user: { user_id: userId },
      },
      select: {
        music_id: true,
        title: true,
        lyrics: true,
        cover: true,
        music_file: true,
        genre: true,
        created_at: true,
        user: { user_id: true, nickname: true },
      },
      order: {
        created_at: 'DESC',
      },
      take: count,
    });
  }

  static async getRecentMusic(): Promise<Music[]> {
    return this.find({
      relations: {
        user: true,
      },
      select: {
        music_id: true,
        title: true,
        lyrics: true,
        cover: true,
        music_file: true,
        genre: true,
        created_at: true,
        user: {
          user_id: true,
          nickname: true,
        },
      },
      order: {
        created_at: 'DESC',
      },
      take: 10,
    });
  }

  static async getMusicById(music_id: string): Promise<Music> {
    return this.findOne({
      relations: { user: true },
      select: { user: { user_id: true, nickname: true } },
      where: { music_id },
    });
  }

  static async getCertainMusicByTitle(keyword: string): Promise<Music[]> {
    return await this.find({
      relations: {
        user: true,
        music_playlist: false,
      },
      select: {
        music_id: true,
        lyrics: true,
        title: true,
        cover: true,
        music_file: true,
        genre: true,
        user: {
          user_id: true,
          nickname: true,
        },
      },
      where: {
        title: ILike(`%${keyword}%`),
      },
      order: {
        created_at: 'DESC',
      },
    });
  }
}
