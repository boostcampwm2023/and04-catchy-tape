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
