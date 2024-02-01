import { Injectable, Logger } from '@nestjs/common';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { Recent_Played } from 'src/entity/recent_played.entity';
import { User } from 'src/entity/user.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { DataSource, ILike, QueryRunner, Repository } from 'typeorm';
import { v4 as uuid } from 'uuid';

@Injectable()
export class UserRepository {
  private userRepository: Repository<User>;
  private readonly logger: Logger = new Logger('UserRepository');

  constructor(private readonly dataSource: DataSource) {
    this.userRepository = this.dataSource.getRepository(User);
  }

  async countNumberOfUserById(user_id: string): Promise<number> {
    return this.userRepository.count({ where: { user_id } });
  }

  async findUserByNickName(nickname: string): Promise<User | null> {
    return this.userRepository.findOneBy({
      nickname,
    });
  }

  async findUserById(user_id: string): Promise<User | null> {
    return this.userRepository.findOne({
      where: { user_id, is_deleted: false },
    });
  }

  async findUserByEmail(user_email: string): Promise<User | null> {
    return this.userRepository.findOneBy({
      user_email,
      is_deleted: false,
    });
  }

  async getCertainUserByNickname(keyword: string): Promise<User[]> {
    return this.userRepository.find({
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

  async updateRecentPlaylist(music_id: string, user_id: string): Promise<void> {
    const queryRunner: QueryRunner = this.dataSource.createQueryRunner();
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

  async updateUserInformation(
    user_id: string,
    image_url: string,
    nickname?: string,
  ): Promise<void> {
    const queryRunner: QueryRunner = this.dataSource.createQueryRunner();
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

  async saveUser(nickname: string, email: string): Promise<void> {
    const queryRunner: QueryRunner = this.dataSource.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      const newUser = this.userRepository.create({
        user_id: uuid(),
        nickname,
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

  async deleteUser(user_id: string): Promise<void> {
    const queryRunner: QueryRunner = this.dataSource.createQueryRunner();
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
