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

  static async isExistMusicId(musicId: string): Promise<boolean> {
    const count: number = await this.count({ where: { music_id: musicId } });
    if (count === 0) {
      return false;
    }
    return true;
  }

  static async isMusicOwner(musicId: string, userId: string): Promise<boolean> {
    const count: number = await this.count({
      where: { music_id: musicId, user: { user_id: userId } },
    });
    if (count === 0) {
      return false;
    }
    return true;
  }
}
