import { Injectable, Logger } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/codes/httpStatusCode.enum';
import { User } from 'src/entity/user.entity';
import { Music } from 'src/entity/music.entity';
import { DataSource, Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/codes/errorCode.enum';
import { Recent_Played } from 'src/entity/recent_played.entity';
import { UserUpdateDto } from '../dto/userUpdate.dto';
import { MusicRepository } from 'src/repository/music.repository';
import { UserRepository } from '../repository/user.repository';
import { Recent_PlayedRepository } from 'src/repository/recent_played.repository';

@Injectable()
export class UserService {
  private readonly logger = new Logger('UserService');
  constructor(
    private musicRepository: MusicRepository,
    private recentPlayedRepository: Recent_PlayedRepository,
    private userRepository: UserRepository,
    private readonly dataSource: DataSource,
  ) {}

  async isDuplicatedUserNickname(userNickname: string): Promise<boolean> {
    try {
      const user = await this.userRepository.findUserByNickName(userNickname);

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

  async deleteUser(user: User): Promise<{ userId: string }> {
    try {
      await this.userRepository.deleteUser(user.user_id);

      return { userId: user.user_id };
    } catch (err) {
      if (err instanceof CatchyException) {
        throw err;
      }

      throw new CatchyException(
        'SERVICE_ERROR',
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
      return await this.recentPlayedRepository.getRecentPlayedMusicByUserId(
        userId,
        count,
      );
    } catch {
      this.logger.error(
        `user.service - getRecentPlayedMusicByUserId : REPOSITORY_ERROR`,
      );
      throw new CatchyException(
        'REPOSITORY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.REPOSITORY_ERROR,
      );
    }
  }

  async updateUserInformation(
    user_id: string,
    userUpdateDto: UserUpdateDto,
  ): Promise<string> {
    try {
      if ((await this.userRepository.countNumberOfUserById(user_id)) == 0) {
        this.logger.error(`user.service - updateUserImage : NOT_EXIST_USER`);
        throw new CatchyException(
          'NOT_EXIST_USER',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.NOT_EXIST_USER,
        );
      }

      const nickname = userUpdateDto.nickname;
      const image_url = userUpdateDto.image_url;

      if (nickname && (await this.isDuplicatedUserNickname(nickname))) {
        throw new CatchyException(
          'DUPLICATED_NICKNAME',
          HTTP_STATUS_CODE.DUPLICATED_NICKNAME,
          ERROR_CODE.DUPLICATED_NICKNAME,
        );
      }

      if (nickname) {
        await this.userRepository.updateUserInformation(
          user_id,
          image_url,
          nickname,
        );
        return user_id;
      }

      await this.userRepository.updateUserInformation(user_id, image_url);

      return user_id;
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
      const user = await this.userRepository.findUserById(user_id);

      if (!user) {
        throw new CatchyException(
          'NOT_EXIST_USER',
          HTTP_STATUS_CODE.NOT_FOUND,
          ERROR_CODE.NOT_EXIST_USER,
        );
      }

      return user;
    } catch (err) {
      if (err instanceof CatchyException) {
        throw err;
      }

      this.logger.error(`user.service - getUserInfomation : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVICE_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getCertainKeywordNicknameUser(keyword: string): Promise<User[]> {
    try {
      return this.userRepository.getCertainUserByNickname(keyword);
    } catch {
      this.logger.error(
        `user.service - getCertainKeywordNicknameUser : REPOSITORY_ERROR`,
      );
      throw new CatchyException(
        'REPOSITORY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.REPOSITORY_ERROR,
      );
    }
  }

  async isExistMusicInRecentPlaylist(
    music_id: string,
    user_id: string,
  ): Promise<boolean> {
    try {
      const musicCount: number =
        await this.recentPlayedRepository.countMusicNumberById(
          music_id,
          user_id,
        );

      return musicCount != 0;
    } catch {
      this.logger.error(
        `user.service - isExistMusicInRecentPlaylist : REPOSITORY_ERROR`,
      );
      throw new CatchyException(
        'REPOSITORY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.REPOSITORY_ERROR,
      );
    }
  }

  async updateRecentMusic(music_id: string, user_id: string): Promise<number> {
    try {
      if (!(await this.musicRepository.getMusicById(music_id))) {
        this.logger.error(
          `user.service - updateRecentMusic : NOT_EXIST_MUSIC_ID`,
        );
        throw new CatchyException(
          'NOT_EXIST_MUSIC_ID',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.NOT_EXIST_MUSIC_ID,
        );
      }
      if (!(await this.isExistMusicInRecentPlaylist(music_id, user_id))) {
        const recent_played_id: number =
          await this.recentPlayedRepository.saveNewRecentMusic(
            music_id,
            user_id,
          );

        return recent_played_id;
      }

      await this.userRepository.updateRecentPlaylist(music_id, user_id);

      return await this.recentPlayedRepository.getRecentPlayedId(
        music_id,
        user_id,
      );
    } catch (err) {
      if (err instanceof CatchyException) throw err;

      this.logger.error(`user.service - updateRecentMusic : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVICE_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getRecentPlaylistMusicCount(user_id: string): Promise<number> {
    try {
      return await this.recentPlayedRepository.getNumberOfRecentPlayedMusic(
        user_id,
      );
    } catch {
      this.logger.error(
        `user.service - getRecentPlaylistMusicCount : REPOSITORY_ERROR`,
      );
      throw new CatchyException(
        'REPOSITORY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.REPOSITORY_ERROR,
      );
    }
  }

  async getRecentPlaylistThumbnail(user_id: string): Promise<string> {
    try {
      const recentMusic =
        await this.recentPlayedRepository.getRecentPlayedMusic(user_id);

      if (recentMusic instanceof Recent_Played) {
        return recentMusic.music.cover;
      }

      throw new CatchyException(
        'NO_RECENT_PLAYED_MUSIC',
        HTTP_STATUS_CODE.NOT_FOUND,
        ERROR_CODE.NO_RECENT_PLAYED_MUSIC,
      );
    } catch (err) {
      if (err instanceof CatchyException) {
        throw err;
      }

      this.logger.error(
        `user.service - getRecentPlaylistThumbnail : REPOSITORY_ERROR`,
      );
      throw new CatchyException(
        'REPOSITORY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.REPOSITORY_ERROR,
      );
    }
  }
}
