import {
  Controller,
  Get,
  Req,
  HttpCode,
  Param,
  UseGuards,
  Patch,
  Body,
  Query,
  Logger,
  Put,
} from '@nestjs/common';
import { UserService } from './user.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { AuthGuard } from '@nestjs/passport';
import { Music } from 'src/entity/music.entity';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { User } from 'src/entity/user.entity';

@Controller('users')
export class UserController {
  private readonly logger = new Logger('User');
  constructor(private userService: UserService) {}

  @Get('duplicate/:name')
  @HttpCode(HTTP_STATUS_CODE.NOT_DUPLICATED_NICKNAME)
  async checkDuplicateNickname(
    @Param('name') name: string,
  ): Promise<{ nickname: string }> {
    this.logger.log(`GET /users/duplicate/${name}`);
    if (await this.userService.isDuplicatedUserEmail(name)) {
      throw new CatchyException(
        'DUPLICATED_NICKNAME',
        HTTP_STATUS_CODE.DUPLICATED_NICKNAME,
        ERROR_CODE.DUPLICATED_NICKNAME,
      );
    }

    return { nickname: name };
  }

  @Get('recent-played')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getUserRecentPlayedMusics(
    @Req() req,
    @Query('count') count: number,
  ): Promise<Music[]> {
    this.logger.log(`GET /users/recent-played - nickname=${req.user.nickname}`);
    const userId = req.user.userId;
    const userMusicData = await this.userService.getRecentPlayedMusicByUserId(
      userId,
      count,
    );

    return userMusicData;
  }

  @Patch('image')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async updateUserImage(
    @Req() req,
    @Body('image_url') image_url,
  ): Promise<{ user_id: string }> {
    this.logger.log(
      `PATCH /users/image - nickname=${req.user.nickname}, image_url=${image_url}`,
    );
    const user_id = req.user.userId;
    return {
      user_id: await this.userService.updateUserImage(user_id, image_url),
    };
  }

  @Get('my-info')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getMyInformation(@Req() req): Promise<User> {
    this.logger.log(`GET /users/my-info - nickname=${req.user.nickname}`);
    const user_id = req.user.userId;
    return this.userService.getUserInformation(user_id);
  }

  @Get('search')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getCertainNicknameUser(
    @Query('keyword') keyword: string,
  ): Promise<User[]> {
    this.logger.log(`GET /users/search - keyword=${keyword}`);
    return this.userService.getCertainKeywordNicknameUser(keyword);
  }

  @Put('recent-played')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async updateRecentPlayMusic(
    @Req() req,
    @Body('musicId') music_id: string,
  ): Promise<{ recent_played_id: number }> {
    this.logger.log(
      `PUT /user/recent-played - nickname=${req.user.nickname}, music_id=${music_id}`,
    );
    const user_id: string = req.user.user_id;
    const recent_played_id: number = await this.userService.updateRecentMusic(
      music_id,
      user_id,
    );
    return { recent_played_id };
  }

  @Get('recent-info')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getRecentPlaylistInfo(
    @Req() req,
  ): Promise<{ music_count: number; thumbnail: string }> {
    const user_id: string = req.user.user_id;
    this.logger.log(`GET /user/recent-info - nickname=${req.user.nickname}`);
    const music_count: number =
      await this.userService.getRecentPlaylistMusicCount(user_id);
    const thumbnail: string =
      await this.userService.getRecentPlaylistThumbnail(user_id);
    return { music_count, thumbnail };
  }
}
