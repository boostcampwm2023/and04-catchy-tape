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
  constructor(private userService: UserService) {}

  @Get('duplicate/:name')
  @HttpCode(HTTP_STATUS_CODE.NOT_DUPLICATED_NICKNAME)
  async checkDuplicateNickname(
    @Param('name') name: string,
  ): Promise<{ nickname: string }> {
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
  async getUserRecentPlayedMusics(@Req() req): Promise<Music[]> {
    const userId = req.user.userId;
    const userMusicData =
      await this.userService.getRecentPlayedMusicByUserId(userId);

    return userMusicData;
  }

  @Patch('image')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async updateUserImage(
    @Req() req,
    @Body('image_url') image_url,
  ): Promise<{ user_id: string }> {
    const user_id = req.user.userId;
    return {
      user_id: await this.userService.updateUserImage(user_id, image_url),
    };
  }

  @Get('my-info')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getMyInformation(@Req() req): Promise<User> {
    const user_id = req.user.userId;
    return this.userService.getUserInformation(user_id);
  }

  @Get('search')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getCertainNicknameUser(
    @Query('keyword') keyword: string,
  ): Promise<User[]> {
    return this.userService.getCertainKeywordNicknameUser(keyword);
  }
}
