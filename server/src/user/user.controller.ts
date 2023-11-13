import {
  Body,
  Get,
  HttpCode,
  HttpException,
  Param,
  Post,
  Res,
  Response,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { UserService } from './user.service';
import { UserCreateDto } from 'src/dto/userCreate.dto';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';

export class UserController {
  constructor(private userService: UserService) {}

  @Post('signup')
  @UsePipes(ValidationPipe)
  @HttpCode(20000)
  signup(@Body() userCreateDto: UserCreateDto): string {
    try {
      const userId: string = this.userService.createUser(userCreateDto);
      return userId;
    } catch {
      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
    }
  }

  @Get('duplicate/:name')
  @HttpCode(HTTP_STATUS_CODE.NOT_DUPLICATED_NICKNAME)
  async checkDuplicateNickname(@Param('name') name: string): Promise<string> {
    if (await this.userService.isDuplicatedUserEmail(name)) {
      throw new HttpException(
        'DUPLICATED_NICKNAME',
        HTTP_STATUS_CODE.DUPLICATED_NICKNAME,
      );
    }

    return name;
  }
}
