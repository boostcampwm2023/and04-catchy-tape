import { Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { User } from 'src/entity/user.entity';
import { Music } from 'src/entity/music.entity';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { PlaylistService } from 'src/playlist/playlist.service';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
    private playlistService: PlaylistService,
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
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getRecentPlayedMusicByUserId(userId: string): Promise<Music[]> {
    return await this.playlistService.getRecentMusicsByUserId(userId);
  }

  async updateUserImage(user_id: string, image_url: string): Promise<string> {
    try {
      const targetUser: User = await this.userRepository.findOne({
        where: { user_id },
      });

      if (!targetUser) {
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
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }
}
