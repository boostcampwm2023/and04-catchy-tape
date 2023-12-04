import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { PlaylistCreateDto } from 'src/dto/playlistCreate.dto';
import { Music } from 'src/entity/music.entity';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { Playlist } from 'src/entity/playlist.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { Repository } from 'typeorm';

@Injectable()
export class PlaylistService {
  constructor(
    @InjectRepository(Playlist)
    private playlistRepository: Repository<Playlist>,
    @InjectRepository(Music_Playlist)
    private music_playlistRepository: Repository<Music_Playlist>,
    @InjectRepository(Music)
    private MusicRepository: Repository<Music>,
  ) {}

  async createPlaylist(
    userId: string,
    playlistCreateDto: PlaylistCreateDto,
  ): Promise<number> {
    try {
      const title: string = playlistCreateDto.title;
      const newPlaylist: Playlist = this.playlistRepository.create({
        playlist_title: title,
        created_at: new Date(),
        updated_at: new Date(),
        user: { user_id: userId },
      });

      const result: Playlist = await this.playlistRepository.save(newPlaylist);
      const playlistId: number = result.playlist_id;
      return playlistId;
    } catch {
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async addMusicToPlaylist(
    userId: string,
    playlistId: number,
    musicId: string,
  ): Promise<number> {
    // 사용자 플리가 있는지 확인
    if (!(await this.isExistPlaylistOnUser(playlistId, userId))) {
      throw new CatchyException(
        'NOT_EXIST_PLAYLIST_ON_USER',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.NOT_EXIST_PLAYLIST_ON_USER,
      );
    }
    // 음악 있는지 확인
    if (!(await this.isExistMusic(musicId))) {
      throw new CatchyException(
        'NOT_EXIST_MUSIC',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.NOT_EXIST_MUSIC,
      );
    }

    // 이미 추가된 음악인지 확인
    if (await this.isAlreadyAdded(playlistId, musicId)) {
      throw new CatchyException(
        'ALREADY_ADDED',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.ALREADY_ADDED,
      );
    }

    // 관계테이블에 추가
    try {
      const new_music_playlist: Music_Playlist =
        this.music_playlistRepository.create({
          music: { music_id: musicId },
          playlist: { playlist_id: playlistId },
          updated_at: new Date(),
        });

      const result: Music_Playlist =
        await this.music_playlistRepository.save(new_music_playlist);
      this.setUpdatedAtNow(playlistId);
      return result.music_playlist_id;
    } catch {
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async isAlreadyAdded(playlistId: number, musicId: string): Promise<boolean> {
    try {
      const count: number = await this.music_playlistRepository.countBy({
        music: { music_id: musicId },
        playlist: { playlist_id: playlistId },
      });
      return count !== 0;
    } catch {
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
      const playlistCount: number = await this.playlistRepository.countBy({
        playlist_id: playlistId,
        user: { user_id: userId },
      });
      return playlistCount !== 0;
    } catch {
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async isExistMusic(musicId: string): Promise<boolean> {
    try {
      const musicCount: number = await this.MusicRepository.countBy({
        music_id: musicId,
      });

      return musicCount !== 0;
    } catch {
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async setUpdatedAtNow(playlistId: number): Promise<void> {
    try {
      const targetPlaylist: Playlist = await this.playlistRepository.findOne({
        where: { playlist_id: playlistId },
      });
      targetPlaylist.updated_at = new Date();
      this.playlistRepository.save(targetPlaylist);
    } catch {
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getUserPlaylists(userId: string): Promise<Playlist[]> {
    try {
      const playlists: Playlist[] = await Playlist.getPlaylistsByUserId(userId);
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
      throw new CatchyException(
        'NOT_EXIST_PLAYLIST_ON_USER',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.NOT_EXIST_PLAYLIST_ON_USER,
      );
    }
    try {
      return Music_Playlist.getMusicListByPlaylistId(playlistId);
    } catch {
      throw new CatchyException(
        'SERVER_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getRecentMusicsByUserId(userId: string) {
    try {
      return Music_Playlist.getRecentPlayedMusicByUserId(userId);
    } catch {
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVER_ERROR,
      );
    }
  }
}
