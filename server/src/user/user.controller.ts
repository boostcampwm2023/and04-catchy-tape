import {
  Controller,
  Get,
  Req,
  HttpCode,
  HttpException,
  Param,
  UseGuards,
} from '@nestjs/common';
import { UserService } from './user.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { AuthGuard } from '@nestjs/passport';
import { Music } from 'src/entity/music.entity';

@Controller('users')
export class UserController {
  constructor(private userService: UserService) {}

  @Get('duplicate/:name')
  @HttpCode(HTTP_STATUS_CODE.NOT_DUPLICATED_NICKNAME)
  async checkDuplicateNickname(
    @Param('name') name: string,
  ): Promise<{ nickname: string }> {
    if (await this.userService.isDuplicatedUserEmail(name)) {
      throw new HttpException(
        'DUPLICATED_NICKNAME',
        HTTP_STATUS_CODE.DUPLICATED_NICKNAME,
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
}
