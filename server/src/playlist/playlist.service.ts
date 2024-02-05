import { Injectable, Logger } from '@nestjs/common';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { PlaylistCreateDto } from 'src/dto/playlistCreate.dto';
import { Music } from 'src/entity/music.entity';
import { Playlist } from 'src/entity/playlist.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { PlaylistRepository } from '../repository/playlist.repository';
import { MusicRepository } from 'src/repository/music.repository';
import { playlistInfo } from 'src/dto/playlistInfo.dto';
import { Music_PlaylistRepository } from 'src/repository/music_playlist.repository';

@Injectable()
export class PlaylistService {
  private readonly logger: Logger = new Logger('PlaylistService');
  constructor(
    private readonly playlistRepository: PlaylistRepository,
    private musicRepository: MusicRepository,
    private music_PlaylistRepository: Music_PlaylistRepository,
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

    return this.music_PlaylistRepository.addMusicToPlaylist(
      musicId,
      playlistId,
    );
  }

  async isAlreadyAdded(playlistId: number, musicId: string): Promise<boolean> {
    try {
      const musicNumber =
        await this.music_PlaylistRepository.countMusicNumberInPlaylist(
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
      const musicCount: number =
        await this.musicRepository.countMusicById(musicId);

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

  async getUserPlaylists(userId: string): Promise<playlistInfo[]> {
    try {
      const playlists: Playlist[] =
        await this.playlistRepository.getPlaylistsByUserId(userId);

      const playlistsInfo: playlistInfo[] = [];

      /*TODO: 이 과정에서 DTO를 추가하게 되어 개선 필요*/
      const countPromises = playlists.map(async (playlist) => {
        const music_count =
          await this.music_PlaylistRepository.getMusicCountByPlaylistId(
            playlist.playlist_id,
          );

        const targetMusic =
          await this.music_PlaylistRepository.getThumbnailByPlaylistId(
            playlist.playlist_id,
          );

        const thumbnail = !targetMusic ? null : targetMusic.music.cover;

        playlistsInfo.push({
          playlist_id: playlist.playlist_id,
          playlist_title: playlist.playlist_title,
          music_count,
          thumbnail,
        });
      });

      await Promise.all(countPromises);

      return playlistsInfo;
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
      return this.music_PlaylistRepository.getMusicListByPlaylistId(playlistId);
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

    return this.playlistRepository.deleteMusicInPlaylist(playlistId, musicId);
  }
}
