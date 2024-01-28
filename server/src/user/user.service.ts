import { Injectable, Logger } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { User } from 'src/entity/user.entity';
import { Music } from 'src/entity/music.entity';
import { DataSource, Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { Recent_Played } from 'src/entity/recent_played.entity';
import { UserUpdateDto } from './../dto/userUpdate.dto';
import { MusicRepository } from 'src/music/music.repository';

@Injectable()
export class UserService {
  private readonly logger = new Logger('UserService');
  constructor(
    private musicRepository: MusicRepository,
    @InjectRepository(Recent_Played)
    private recentPlayedRepository: Repository<Recent_Played>,
    private readonly dataSource: DataSource,
  ) {}

  async isDuplicatedUserNickname(userNickname: string): Promise<boolean> {
    try {
      const user = await User.findUserByNickName(userNickname);

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

  async updateUserInformation(
    user_id: string,
    userUpdateDto: UserUpdateDto,
  ): Promise<string> {
    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.startTransaction();
    try {
      if ((await User.countNumberOfUserById(user_id)) == 0) {
        this.logger.error(`user.service - updateUserImage : NOT_EXIST_USER`);
        throw new CatchyException(
          'NOT_EXIST_USER',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.NOT_EXIST_USER,
        );
      }

      const nickname = userUpdateDto.nickname;
      const image_url = userUpdateDto.image_url;

      if (await this.isDuplicatedUserNickname(nickname)) {
        throw new CatchyException(
          'DUPLICATED_NICKNAME',
          HTTP_STATUS_CODE.DUPLICATED_NICKNAME,
          ERROR_CODE.DUPLICATED_NICKNAME,
        );
      }

      await queryRunner.manager.update(
        User,
        { user_id },
        { photo: image_url, nickname: nickname },
      );
      await queryRunner.commitTransaction();
      return user_id;
    } catch (err) {
      await queryRunner.rollbackTransaction();
      if (err instanceof CatchyException) {
        throw err;
      }

      this.logger.error(`user.service - updateUserImage : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }

  async getUserInformation(user_id: string): Promise<User> {
    try {
      return await User.findUserById(user_id);
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

  async isExistMusicInRecentPlaylist(
    music_id: string,
    user_id: string,
  ): Promise<boolean> {
    try {
      const musicCount: number = await Recent_Played.countMusicNumberById(
        music_id,
        user_id,
      );

      return musicCount != 0;
    } catch {
      this.logger.error(
        `user.service - isExistMusicInRecentPlaylist : QUERY_ERROR`,
      );
      throw new CatchyException(
        'QUERY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.QUERY_ERROR,
      );
    }
  }

  async updateRecentMusic(music_id: string, user_id: string): Promise<number> {
    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.startTransaction();
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
        const newRow: Recent_Played = this.recentPlayedRepository.create({
          music: { music_id },
          user: { user_id },
          played_at: new Date(),
        });
        const addedRow: Recent_Played =
          await this.recentPlayedRepository.save(newRow);
        return addedRow.recent_played_id;
      }

      await queryRunner.manager.update(
        Recent_Played,
        { music: { music_id }, user: { user_id } },
        { played_at: new Date() },
      );
      await queryRunner.commitTransaction();

      return await Recent_Played.getRecentPlayedId(music_id, user_id);
    } catch (err) {
      await queryRunner.rollbackTransaction();
      if (err instanceof CatchyException) throw err;

      this.logger.error(`user.service - updateRecentMusic : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVICE_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }

  async getRecentPlaylistMusicCount(user_id: string): Promise<number> {
    try {
      return await Recent_Played.getNumberOfRecentPlayedMusic(user_id);
    } catch {
      this.logger.error(
        `user.service - getRecentPlaylistMusicCount : QUERY_ERROR`,
      );
      throw new CatchyException(
        'QUERY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.QUERY_ERROR,
      );
    }
  }

  async getRecentPlaylistThumbnail(user_id: string): Promise<string> {
    try {
      const recentMusic = await Recent_Played.getRecentPlayedMusic(user_id);

      return recentMusic.music.cover;
    } catch {
      this.logger.error(
        `user.service - getRecentPlaylistThumbnail : QUERY_ERROR`,
      );
      throw new CatchyException(
        'QUERY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.QUERY_ERROR,
      );
    }
  }
}
