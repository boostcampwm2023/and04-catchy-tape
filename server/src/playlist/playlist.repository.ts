import { Injectable, Logger } from '@nestjs/common';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { PlaylistCreateDto } from 'src/dto/playlistCreate.dto';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { Playlist } from 'src/entity/playlist.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { DataSource, QueryRunner, Repository } from 'typeorm';

@Injectable()
export class PlaylistRepository {
  private playlistRepository: Repository<Playlist>;
  private music_playlistRepository: Repository<Music_Playlist>;
  private readonly logger: Logger = new Logger('PlaylistRepository');

  constructor(private readonly dataSource: DataSource) {
    this.playlistRepository = this.dataSource.getRepository(Playlist);
    this.music_playlistRepository =
      this.dataSource.getRepository(Music_Playlist);
  }

  async isExistPlaylistOnUser(
    userId: string,
    playlistId: number,
  ): Promise<number> {
    return this.playlistRepository.countBy({
      playlist_id: playlistId,
      user: { user_id: userId },
    });
  }

  async getPlaylistsByUserId(userId: string): Promise<Playlist[]> {
    return await this.playlistRepository.find({
      select: { playlist_id: true, playlist_title: true },
      where: {
        user: { user_id: userId },
      },
      order: {
        updated_at: 'DESC',
      },
    });
  }

  async createPlaylist(
    userId: string,
    playlistCreateDto: PlaylistCreateDto,
  ): Promise<number> {
    const queryRunner: QueryRunner = this.dataSource.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      const title: string = playlistCreateDto.title;
      const newPlaylist: Playlist = this.playlistRepository.create({
        playlist_title: title,
        created_at: new Date(),
        updated_at: new Date(),
        user: { user_id: userId },
      });

      const result: Playlist = await queryRunner.manager.save(newPlaylist);
      await queryRunner.commitTransaction();

      const playlistId: number = result.playlist_id;

      return playlistId;
    } catch {
      await queryRunner.rollbackTransaction();

      this.logger.error(`playlist.repository - createPlaylist : ENTITY_ERROR`);
      throw new CatchyException(
        'ENTITY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.ENTITY_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }

  async deleteSinglePlaylist(
    userId: string,
    playlistId: number,
  ): Promise<number> {
    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      await queryRunner.manager.delete(Music_Playlist, {
        playlist: {
          playlist_id: playlistId,
        },
      });

      await queryRunner.manager.delete(Playlist, {
        playlist_id: playlistId,
        user: { user_id: userId },
      });

      await queryRunner.commitTransaction();
      return playlistId;
    } catch (error) {
      await queryRunner.rollbackTransaction();

      if (error instanceof CatchyException) {
        throw error;
      }

      this.logger.error(
        `playlist.repository - deleteSinglePlaylist : SERVICE_ERROR`,
      );
      throw new CatchyException(
        'SERVICE_ERROR',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.SERVICE_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }

  async deleteMusicInPlaylist(
    playlistId: number,
    musicId: string,
  ): Promise<number> {
    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      const target = await this.music_playlistRepository.findOne({
        where: {
          music: { music_id: musicId },
          playlist: { playlist_id: playlistId },
        },
      });

      if (target == undefined) {
        this.logger.error(
          `playlist.service - deleteMusicInPlaylist : NOT_ADDED_MUSIC`,
        );
        throw new CatchyException(
          'NOT_ADDED_MUSIC',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.NOT_ADDED_MUSIC,
        );
      }

      const deletedMusicPlaylistId: number = target.music_playlist_id;

      await queryRunner.manager.remove(target);

      await queryRunner.commitTransaction();

      return deletedMusicPlaylistId;
    } catch (error) {
      await queryRunner.rollbackTransaction();

      if (error instanceof CatchyException) {
        throw error;
      }

      this.logger.error(
        `playlist.service - deleteMusicInPlaylist : SERVICE_ERROR`,
      );
      throw new CatchyException(
        'SERVICE_ERROR',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.SERVICE_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }
}
