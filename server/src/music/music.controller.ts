import {
  Body,
  Controller,
  Req,
  HttpCode,
  Post,
  Get,
  UseGuards,
  UsePipes,
  ValidationPipe,
  Query,
  Logger,
} from '@nestjs/common';
import { MusicService } from './music.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { AuthGuard } from '@nestjs/passport';
import { Genres } from 'src/constants';
import { Music } from 'src/entity/music.entity';

@Controller('musics')
export class MusicController {
  private readonly logger = new Logger('Music');
  private objectStorage: AWS.S3;
  constructor(
    private readonly musicService: MusicService,
  ) {}

  @Post()
  @UsePipes(ValidationPipe)
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async upload(
    @Body() musicCreateDto: MusicCreateDto,
    @Req() req,
  ): Promise<{ music_id: string }> {
    const userId = req.user.user_id;
    this.logger.log(`POST /musics - nickname=${req.user.nickname}`);
    const savedMusicId: string = await this.musicService.createMusic(
      musicCreateDto,
      userId,
    );
    return { music_id: savedMusicId };
  }

  @Get('recent-uploads')
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getRecentMusics(): Promise<Music[]> {
    this.logger.log(`GET /musics/recent-uploads`);
    const musics = this.musicService.getRecentMusic();
    return musics;
  }

  @Get('genres')
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  getGenres(): { genres: string[] } {
    this.logger.log(`GET /musics/genres`);
    const genreName: string[] = Object.keys(Genres);
    return { genres: genreName };
  }

  @Get('my-uploads')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getMyUploads(
    @Req() req,
    @Query('count') count: number,
  ): Promise<Music[]> {
    this.logger.log(
      `GET /musics/my-uploads - nickname=${req.user.nickname}, count=${count}`,
    );
    const userId: string = req.user.user_id;
    return this.musicService.getMyUploads(userId, count);
  }

  @Get('info')
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getMusicInfo(@Query('music_id') music_id: string): Promise<Music> {
    this.logger.log(`GET /musics/info - music_id=${music_id}`);
    return this.musicService.getMusicInfo(music_id);
  }

  @Get('ts')
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getEncodedChunkFiles(
    @Query('music_id') music_id: string,
    @Query('fileName') fileName: string,
  ): Promise<{ file: AWS.S3.Body }> {
    this.logger.log(
      `GET /musics/ts - music_id=${music_id}, fileName=${fileName}`,
    );
    return {
      file: await this.musicService.getEncodedChunkFiles(music_id, fileName),
    };
  }

  @Get('search')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getCertainTitleMusic(
    @Query('keyword') keyword: string,
  ): Promise<Music[]> {
    this.logger.log(`GET /musics/search - keyword=${keyword}`);
    return this.musicService.getCertainKeywordNicknameUser(keyword);
  }
}
