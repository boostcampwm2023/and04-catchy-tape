import {
  Body,
  Controller,
  Get,
  HttpCode,
  HttpException,
  Param,
  Post,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { UserService } from './user.service';
import { UserCreateDto } from 'src/dto/userCreate.dto';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';

@Controller('user')
export class UserController {
  constructor(private userService: UserService) {}

  @Post('signup')
  @UsePipes(ValidationPipe)
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  signup(@Body() userCreateDto: UserCreateDto): { userId: string } {
    try {
      const userId: string = this.userService.createUser(userCreateDto);
      return { userId };
    } catch (err) {
      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
    }
  }

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
