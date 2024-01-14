import {
  Column,
  Entity,
  CreateDateColumn,
  BaseEntity,
  PrimaryColumn,
  OneToMany,
  ILike,
} from 'typeorm';
import { Playlist } from './playlist.entity';
import { Music } from './music.entity';
import { Recent_Played } from './recent_played.entity';

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

  @Column({ default: false })
  is_deleted: boolean;

  @OneToMany(() => Music, (music) => music.user)
  musics: Music[];

  @OneToMany(() => Playlist, (playlist) => playlist.user)
  playlists: Playlist[];

  @OneToMany(() => Recent_Played, (recent_played) => recent_played.user)
  recent_played: Recent_Played[];

  static async countNumberOfUserById(user_id: string): Promise<number> {
    return this.count({ where: { user_id } });
  }

  static async findUserByNickName(nickname: string): Promise<User> {
    return this.findOneBy({
      nickname,
    });
  }

  static async findUserById(user_id: string): Promise<User> {
    return this.findOne({
      where: { user_id, is_deleted: false },
    });
  }

  static async findUserByEmail(user_email: string): Promise<User> {
    return this.findOneBy({
      user_email,
      is_deleted: false,
    });
  }

  static async getCertainUserByNickname(keyword: string): Promise<User[]> {
    return this.find({
      relations: {
        musics: false,
        playlists: false,
      },
      select: {
        user_id: true,
        nickname: true,
        user_email: true,
        photo: true,
        created_at: true,
      },
      where: {
        nickname: ILike(`%${keyword}%`),
      },
    });
  }
}
