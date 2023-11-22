import {
  Body,
  Controller,
  Get,
  HttpCode,
  Param,
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
    @Body('musicId') musicId: number,
  ): Promise<{ music_playlist_id: number }> {
    const userId: string = req.user.user_id;
    const music_playlist_id: number =
      await this.playlistService.addMusicToPlaylist(
        userId,
        playlistId,
        musicId,
      );
    return { music_playlist_id: music_playlist_id };
  }

  @Get()
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getUserPlaylists(@Req() req) {
    const userId: string = req.user.user_id;
    const playlists: Playlist[] =
      await this.playlistService.getUserPlaylists(userId);
    return playlists;
  }
}
