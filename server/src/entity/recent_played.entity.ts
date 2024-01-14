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
import { User } from './user.entity';

@Entity({ name: 'recent_played' })
@Index(['user', 'music'])
export class Recent_Played extends BaseEntity {
  @PrimaryGeneratedColumn()
  recent_played_id: number;

  @ManyToOne(() => Music, (music) => music.recent_played, {
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'music_id' })
  music: Music;

  @ManyToOne(() => User, (user) => user.recent_played)
  @JoinColumn({ name: 'user_id' })
  user: User;

  @Column()
  @Index()
  played_at: Date;

  static async countMusicNumberById(
    music_id: string,
    user_id: string,
  ): Promise<number> {
    return this.count({
      where: { music: { music_id }, user: { user_id } },
    });
  }

  static async getRecentPlayedId(
    music_id: string,
    user_id: string,
  ): Promise<number> {
    return (
      await this.findOne({
        where: { music: { music_id }, user: { user_id } },
      })
    ).recent_played_id;
  }

  static async getNumberOfRecentPlayedMusic(user_id: string): Promise<number> {
    return this.count({
      where: { user: { user_id } },
    });
  }

  static async getRecentPlayedMusic(user_id: string): Promise<Recent_Played> {
    return this.findOne({
      relations: { music: true, user: true },
      select: { music: { cover: true } },
      where: { user: { user_id } },
      order: { played_at: 'DESC' },
    });
  }

  static async getRecentPlayedMusicByUserId(
    user_id: string,
    count: number,
  ): Promise<Music[]> {
    return await this.find({
      relations: {
        music: { user: true },
        user: true,
      },
      where: {
        user: { user_id },
      },
      select: {
        recent_played_id: false,
        music: {
          music_id: true,
          title: true,
          music_file: true,
          cover: true,
          genre: true,
          user: { user_id: true, nickname: true },
        },
      },
      order: {
        played_at: 'DESC',
      },
      take: count,
    }).then((recent_played: Recent_Played[]) =>
      recent_played.map((recent) => recent.music),
    );
  }
}
