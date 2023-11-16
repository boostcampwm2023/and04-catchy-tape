import {
  Controller,
  Get,
  HttpCode,
  HttpException,
  Param,
} from '@nestjs/common';
import { UserService } from './user.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';

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
}
