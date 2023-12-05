import {
  Body,
  Controller,
  Get,
  HttpCode,
  Param,
  Patch,
  Post,
  Req,
  UseGuards,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { PlaylistService } from './playlist.service';
import { AuthGuard } from '@nestjs/passport';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { PlaylistCreateDto } from 'src/dto/playlistCreate.dto';
import { Playlist } from 'src/entity/playlist.entity';
import { Music } from 'src/entity/music.entity';

@Controller('playlists')
export class PlaylistController {
  constructor(private playlistService: PlaylistService) {}

  @Post()
  @UseGuards(AuthGuard())
  @UsePipes(ValidationPipe)
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async createPlaylist(
    @Req() req,
    @Body() playlistCreateDto: PlaylistCreateDto,
  ): Promise<{ playlist_id: number }> {
    const userId: string = req.user.user_id;
    const playlistId: number = await this.playlistService.createPlaylist(
      userId,
      playlistCreateDto,
    );
    return { playlist_id: playlistId };
  }

  @Post(':playlistId')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async addMusicToPlaylist(
    @Req() req,
    @Param('playlistId') playlistId: number,
    @Body('musicId') music_id: string,
  ): Promise<{ music_playlist_id: number }> {
    const userId: string = req.user.user_id;
    const music_playlist_id: number =
      await this.playlistService.addMusicToPlaylist(
        userId,
        playlistId,
        music_id,
      );
    return { music_playlist_id };
  }

  @Get()
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getUserPlaylists(@Req() req): Promise<Playlist[]> {
    const userId: string = req.user.user_id;
    const playlists: Playlist[] =
      await this.playlistService.getUserPlaylists(userId);
    return playlists;
  }

  @Get(':playlistId')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getPlaylistMusics(
    @Req() req,
    @Param('playlistId') playlistId: number,
  ): Promise<Music[]> {
    const userId: string = req.user.user_id;
    return await this.playlistService.getPlaylistMusics(userId, playlistId);
  }

  @Patch('recent-played')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async updateRecentPlay(
    @Req() req,
    @Body('musicId') music_id: string,
  ): Promise<{ music_playlist_id: number }> {
    const user_id: string = req.user.user_id;
    const recentPlaylist: Playlist =
      await this.playlistService.getRecentPlaylist(user_id);
    const recentPlaylistId: number = recentPlaylist.playlist_id;
    if (await this.playlistService.isAlreadyAdded(recentPlaylistId, music_id)) {
      return {
        music_playlist_id: await this.playlistService.updateRecentMusic(
          music_id,
          recentPlaylistId,
        ),
      };
    }
    return {
      music_playlist_id: await this.playlistService.addMusicToPlaylist(
        user_id,
        recentPlaylistId,
        music_id,
      ),
    };
  }
}
