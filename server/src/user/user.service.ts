import { Injectable, Logger } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { User } from 'src/entity/user.entity';
import { Music } from 'src/entity/music.entity';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { Recent_Played } from 'src/entity/recent_played.entity';

@Injectable()
export class UserService {
  private readonly logger = new Logger('UserService');
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
  ) {}

  async isDuplicatedUserEmail(userNickname: string): Promise<boolean> {
    try {
      const user = await this.userRepository.findOneBy({
        nickname: userNickname,
      });

      if (user) {
        return true;
      }

      return false;
    } catch {
      this.logger.error(`user.service - isDuplicatedUserEmail : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getRecentPlayedMusicByUserId(
    userId: string,
    count: number,
  ): Promise<Music[]> {
    try {
      return await Recent_Played.getRecentPlayedMusicByUserId(userId, count);
    } catch {
      this.logger.error(
        `user.service - getRecentPlayedMusicByUserId : QUERY_ERROR`,
      );
      throw new CatchyException(
        'QUERY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.QUERY_ERROR,
      );
    }
  }

  async updateUserImage(user_id: string, image_url: string): Promise<string> {
    try {
      const targetUser: User = await this.userRepository.findOne({
        where: { user_id },
      });

      if (!targetUser) {
        this.logger.error(`user.service - updateUserImage : NOT_EXIST_USER`);
        throw new CatchyException(
          'NOT_EXIST_USER',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.NOT_EXIST_USER,
        );
      }

      targetUser.photo = image_url;
      const savedUser: User = await this.userRepository.save(targetUser);
      return savedUser.user_id;
    } catch (err) {
      if (err instanceof CatchyException) {
        throw err;
      }

      this.logger.error(`user.service - updateUserImage : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getUserInformation(user_id: string): Promise<User> {
    try {
      return await this.userRepository.findOne({
        where: { user_id },
      });
    } catch {
      this.logger.error(`user.service - getUserInfomation : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getCertainKeywordNicknameUser(keyword: string): Promise<User[]> {
    try {
      return User.getCertainUserByNickname(keyword);
    } catch {
      this.logger.error(
        `user.service - getCertainKeywordNicknameUser : QUERY_ERROR`,
      );
      throw new CatchyException(
        'QUERY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.QUERY_ERROR,
      );
    }
  }
}
