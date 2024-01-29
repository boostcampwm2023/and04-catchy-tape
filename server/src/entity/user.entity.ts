import {
  Column,
  Entity,
  CreateDateColumn,
  BaseEntity,
  PrimaryColumn,
  OneToMany,
  ILike,
  EntityManager,
} from 'typeorm';
import { Playlist } from './playlist.entity';
import { Music } from './music.entity';
import { Recent_Played } from './recent_played.entity';
import { v4 as uuid } from 'uuid';
import { Logger } from '@nestjs/common';
import { CatchyException } from 'src/config/catchyException';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { ERROR_CODE } from 'src/config/errorCode.enum';

@Entity({ name: 'user' })
export class User extends BaseEntity {
  private static readonly logger: Logger = new Logger('MusicEntity');

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

  static async findUserByNickName(nickname: string): Promise<User | null> {
    return this.findOneBy({
      nickname,
    });
  }

  static async findUserById(user_id: string): Promise<User | null> {
    return this.findOne({
      where: { user_id, is_deleted: false },
    });
  }

  static async findUserByEmail(user_email: string): Promise<User | null> {
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

  static async updateRecentPlaylist(
    music_id: string,
    user_id: string,
  ): Promise<void> {
    const entityManager: EntityManager = this.getRepository().manager;
    const queryRunner = entityManager.connection.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      await queryRunner.manager.update(
        Recent_Played,
        { music: { music_id }, user: { user_id } },
        { played_at: new Date() },
      );

      await queryRunner.commitTransaction();
    } catch {
      await queryRunner.rollbackTransaction();

      this.logger.error(`user.entity - updateRecentPlaylist : ENTITY_ERROR`);
      throw new CatchyException(
        'ENTITY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.ENTITY_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }

  static async updateUserInformation(
    user_id: string,
    image_url: string,
    nickname?: string,
  ): Promise<void> {
    const entityManager: EntityManager = this.getRepository().manager;
    const queryRunner = entityManager.connection.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      await queryRunner.manager.update(
        User,
        { user_id },
        { photo: image_url, nickname },
      );

      await queryRunner.commitTransaction();
    } catch {
      await queryRunner.rollbackTransaction();

      this.logger.error(`user.entity - updateUserInformation : ENTITY_ERROR`);
      throw new CatchyException(
        'ENTITY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.ENTITY_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }

  static async saveUser(nickname: string, email: string): Promise<void> {
    const entityManager: EntityManager = this.getRepository().manager;
    const queryRunner = entityManager.connection.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      const newUser = this.create({
        user_id: uuid(),
        nickname,
        photo: null,
        user_email: email,
        created_at: new Date(),
      });

      await queryRunner.manager.save(newUser);

      await queryRunner.commitTransaction();
    } catch {
      await queryRunner.rollbackTransaction();

      this.logger.error(`user.entity - saveUser : ENTITY_ERROR`);
      throw new CatchyException(
        'ENTITY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.ENTITY_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }

  static async deleteUser(user_id: string): Promise<void> {
    const entityManager: EntityManager = this.getRepository().manager;
    const queryRunner = entityManager.connection.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      await queryRunner.manager.update(User, { user_id }, { is_deleted: true });

      await queryRunner.commitTransaction();
    } catch {
      await queryRunner.rollbackTransaction();

      this.logger.error(`user.entity - deleteUser : ENTITY_ERROR`);
      throw new CatchyException(
        'ENTITY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.ENTITY_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }
}
