import {
  Controller,
  Get,
  Req,
  HttpCode,
  Param,
  UseGuards,
  UsePipes,
  Patch,
  Body,
  Query,
  Logger,
  Put,
  ValidationPipe,
} from '@nestjs/common';
import { UserService } from './user.service';
import { HTTP_STATUS_CODE } from 'src/codes/httpStatusCode.enum';
import { AuthGuard } from '@nestjs/passport';
import { Music } from 'src/entity/music.entity';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/codes/errorCode.enum';
import { User } from 'src/entity/user.entity';
import { UserUpdateDto } from 'src/dto/userUpdate.dto';
import { ReqUser } from 'src/config/decorators';

@Controller('users')
export class UserController {
  private readonly logger = new Logger('User');
  constructor(private userService: UserService) {}

  @Patch()
  @UseGuards(AuthGuard())
  @UsePipes(ValidationPipe)
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async updateUserImage(
    @ReqUser() user: User,
    @Body() userUpdateDto: UserUpdateDto,
  ): Promise<{ user_id: string }> {
    this.logger.log(
      `PATCH /users - nickname=${user.nickname}->${userUpdateDto.nickname}, image_url=${userUpdateDto.image_url}`,
    );
    const user_id = user.user_id;
    return {
      user_id: await this.userService.updateUserInformation(
        user_id,
        userUpdateDto,
      ),
    };
  }

  @Get('duplicate/:name')
  @HttpCode(HTTP_STATUS_CODE.NOT_DUPLICATED_NICKNAME)
  async checkDuplicateNickname(
    @Param('name') name: string,
  ): Promise<{ nickname: string }> {
    this.logger.log(`GET /users/duplicate/${name}`);
    if (await this.userService.isDuplicatedUserNickname(name)) {
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
    @ReqUser() user: User,
    @Query('count') count: number,
  ): Promise<Music[]> {
    this.logger.log(`GET /users/recent-played - nickname=${user.nickname}`);
    const userId = user.user_id;
    const userMusicData = await this.userService.getRecentPlayedMusicByUserId(
      userId,
      count,
    );

    return userMusicData;
  }

  @Get('my-info')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getMyInformation(@ReqUser() user: User): Promise<User> {
    this.logger.log(`GET /users/my-info - nickname=${user.nickname}`);
    const user_id = user.user_id;
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
    @ReqUser() user: User,
    @Body('musicId') music_id: string,
  ): Promise<{ recent_played_id: number }> {
    this.logger.log(
      `PUT /user/recent-played - nickname=${user.nickname}, music_id=${music_id}`,
    );
    const user_id: string = user.user_id;
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
    @ReqUser() user: User,
  ): Promise<{ music_count: number; thumbnail: string }> {
    const user_id: string = user.user_id;
    this.logger.log(`GET /user/recent-info - nickname=${user.nickname}`);
    const music_count: number =
      await this.userService.getRecentPlaylistMusicCount(user_id);
    const thumbnail: string =
      await this.userService.getRecentPlaylistThumbnail(user_id);
    return { music_count, thumbnail };
  }
}
