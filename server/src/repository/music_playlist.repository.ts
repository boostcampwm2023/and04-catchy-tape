import { Injectable, Logger } from '@nestjs/common';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { Music } from 'src/entity/music.entity';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { Playlist } from 'src/entity/playlist.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { DataSource, QueryRunner, Repository } from 'typeorm';

@Injectable()
export class Music_PlaylistRepository {
  private music_PlaylistRepository: Repository<Music_Playlist>;
  private readonly logger: Logger = new Logger('Music_PlaylistRepository');

  constructor(private readonly dataSource: DataSource) {
    this.music_PlaylistRepository =
      this.dataSource.getRepository(Music_Playlist);
  }

  async getMusicCountByPlaylistId(playlist_id: number): Promise<number> {
    return this.music_PlaylistRepository.count({
      where: { playlist: { playlist_id } },
    });
  }

  async countMusicNumberInPlaylist(
    musicId: string,
    playlistId: number,
  ): Promise<number> {
    return this.music_PlaylistRepository.countBy({
      music: { music_id: musicId },
      playlist: { playlist_id: playlistId },
    });
  }

  async getThumbnailByPlaylistId(
    playlist_id: number,
  ): Promise<Music_Playlist | null> {
    return this.music_PlaylistRepository.findOne({
      relations: { music: true },
      select: { music: { cover: true } },
      where: { playlist: { playlist_id } },
      order: { created_at: 'DESC' },
    });
  }

  async getMusicListByPlaylistId(playlistId: number): Promise<Music[]> {
    return this.music_PlaylistRepository
      .find({
        relations: {
          music: { user: true },
        },
        where: {
          playlist: { playlist_id: playlistId },
        },
        select: {
          music: {
            music_id: true,
            title: true,
            cover: true,
            music_file: true,
            genre: true,
            user: { user_id: true, nickname: true },
          },
          music_playlist_id: false,
        },
        order: {
          created_at: 'DESC',
        },
      })
      .then((a: Music_Playlist[]) => a.map((b) => b.music));
  }

  async addMusicToPlaylist(
    musicId: string,
    playlistId: number,
  ): Promise<number> {
    const queryRunner: QueryRunner = this.dataSource.createQueryRunner();
    await queryRunner.startTransaction();
    // 관계테이블에 추가
    try {
      const new_music_playlist: Music_Playlist =
        this.music_PlaylistRepository.create({
          music: { music_id: musicId },
          playlist: { playlist_id: playlistId },
          created_at: new Date(),
        });

      await queryRunner.manager.save(new_music_playlist);
      await queryRunner.manager.update(
        Playlist,
        { playlist_id: playlistId },
        { updated_at: new Date() },
      );

      await queryRunner.commitTransaction();

      return new_music_playlist.music_playlist_id;
    } catch {
      await queryRunner.rollbackTransaction();

      this.logger.error(
        `playlist.repository - addMusicToPlaylist : REPOSITORY_ERROR`,
      );
      throw new CatchyException(
        'REPOSITORY_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.REPOSITORY_ERROR,
      );
    } finally {
      await queryRunner.release();
    }
  }
}
