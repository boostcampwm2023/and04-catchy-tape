import { Injectable, Logger } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { PlaylistCreateDto } from 'src/dto/playlistCreate.dto';
import { Music } from 'src/entity/music.entity';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { Playlist } from 'src/entity/playlist.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { DataSource, Repository } from 'typeorm';
import { PlaylistRepository } from './playlist.repository';

@Injectable()
export class PlaylistService {
  private readonly logger: Logger = new Logger('PlaylistService');
  constructor(
    private readonly playlistRepository: PlaylistRepository,
    @InjectRepository(Music_Playlist)
    private music_playlistRepository: Repository<Music_Playlist>,
    private readonly dataSource: DataSource,
  ) {}

  async createPlaylist(
    userId: string,
    playlistCreateDto: PlaylistCreateDto,
  ): Promise<number> {
    return this.playlistRepository.createPlaylist(userId, playlistCreateDto);
  }

  async addMusicToPlaylist(
    userId: string,
    playlistId: number,
    musicId: string,
  ): Promise<number> {
    // 사용자 플리가 있는지 확인
    if (!(await this.isExistPlaylistOnUser(playlistId, userId))) {
      this.logger.error(
        `playlist.service - addMusicToPlaylist : NOT_EXIST_PLAYLIST_ON_USER`,
      );
      throw new CatchyException(
        'NOT_EXIST_PLAYLIST_ON_USER',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.NOT_EXIST_PLAYLIST_ON_USER,
      );
    }
    // 음악 있는지 확인
    if (!(await this.isExistMusic(musicId))) {
      this.logger.error(
        `playlist.service - addMusicToPlaylist : NOT_EXIST_MUSIC`,
      );
      throw new CatchyException(
        'NOT_EXIST_MUSIC',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.NOT_EXIST_MUSIC,
      );
    }

    // 이미 추가된 음악인지 확인
    if (await this.isAlreadyAdded(playlistId, musicId)) {
      this.logger.error(
        `playlist.service - addMusicToPlaylist : ALREADY_ADDED`,
      );
      throw new CatchyException(
        'ALREADY_ADDED',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.ALREADY_ADDED,
      );
    }

    return Music_Playlist.addMusicToPlaylist(musicId, playlistId);
  }

  async isAlreadyAdded(playlistId: number, musicId: string): Promise<boolean> {
    try {
      const musicNumber = await Music_Playlist.countMusicNumberInPlaylist(
        musicId,
        playlistId,
      );

      return musicNumber !== 0;
    } catch {
      this.logger.error(`playlist.service - isAlreadyAdded : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async isExistPlaylistOnUser(
    playlistId: number,
    userId: string,
  ): Promise<boolean> {
    try {
      const playlistNumber =
        await this.playlistRepository.isExistPlaylistOnUser(userId, playlistId);

      return playlistNumber !== 0;
    } catch {
      this.logger.error(
        `playlist.service - isExistPlaylistOnUser : SERVICE_ERROR`,
      );
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async isExistMusic(musicId: string): Promise<boolean> {
    try {
      const musicCount: number = await Music.countMusicById(musicId);

      return musicCount !== 0;
    } catch {
      this.logger.error(`playlist.service - isExistMusic : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getUserPlaylists(userId: string): Promise<Playlist[]> {
    try {
      const playlists: Playlist[] =
        await this.playlistRepository.getPlaylistsByUserId(userId);

      const countPromises = playlists.map(async (playlist) => {
        playlist['music_count'] =
          await Music_Playlist.getMusicCountByPlaylistId(playlist.playlist_id);
      });

      const thumbnailPromises = playlists.map(async (playlist) => {
        const target = await Music_Playlist.getThumbnailByPlaylistId(
          playlist.playlist_id,
        );
        playlist['thumbnail'] = !target ? null : target.music.cover;
      });

      await Promise.all(countPromises);
      await Promise.all(thumbnailPromises);

      return playlists;
    } catch {
      this.logger.error(`playlist.service - getUserPlaylists : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getPlaylistMusics(
    userId: string,
    playlistId: number,
  ): Promise<Music[]> {
    if (!(await this.isExistPlaylistOnUser(playlistId, userId))) {
      this.logger.error(
        `playlist.service - getPlaylistMusics : NOT_EXIST_PLAYLIST_ON_USER`,
      );
      throw new CatchyException(
        'NOT_EXIST_PLAYLIST_ON_USER',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.NOT_EXIST_PLAYLIST_ON_USER,
      );
    }
    try {
      return Music_Playlist.getMusicListByPlaylistId(playlistId);
    } catch {
      this.logger.error(`playlist.service - getPlaylistMusics : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async deleteSinglePlaylist(
    userId: string,
    playlistId: number,
  ): Promise<number> {
    // 사용자 플리가 있는지 확인
    if (!(await this.isExistPlaylistOnUser(playlistId, userId))) {
      this.logger.error(
        `playlist.service - deleteSinglePlaylist : NOT_EXIST_PLAYLIST_ON_USER`,
      );
      throw new CatchyException(
        'NOT_EXIST_PLAYLIST_ON_USER',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.NOT_EXIST_PLAYLIST_ON_USER,
      );
    }

    return this.playlistRepository.deleteSinglePlaylist(userId, playlistId);
  }

  async deleteMusicInPlaylist(
    userId: string,
    playlistId: number,
    musicId: string,
  ): Promise<number> {
    // 사용자 플리가 있는지 확인
    if (!(await this.isExistPlaylistOnUser(playlistId, userId))) {
      this.logger.error(
        `playlist.service - deleteMusicInPlaylist : NOT_EXIST_PLAYLIST_ON_USER`,
      );
      throw new CatchyException(
        'NOT_EXIST_PLAYLIST_ON_USER',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.NOT_EXIST_PLAYLIST_ON_USER,
      );
    }
    // 음악 있는지 확인
    if (!(await this.isExistMusic(musicId))) {
      this.logger.error(
        `playlist.service - deleteMusicInPlaylist : NOT_EXIST_MUSIC`,
      );
      throw new CatchyException(
        'NOT_EXIST_MUSIC',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.NOT_EXIST_MUSIC,
      );
    }

    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.startTransaction();

    try {
      const target: Music_Playlist =
        await this.music_playlistRepository.findOne({
          where: {
            music: { music_id: musicId },
            playlist: { playlist_id: playlistId },
          },
        });

      const deletedMusicPlaylistId: number = target.music_playlist_id;

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
