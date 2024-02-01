import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  Logger,
  Post,
  UseGuards,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { UserCreateDto } from 'src/dto/userCreate.dto';
import { AuthGuard } from '@nestjs/passport';
import { User } from 'src/entity/user.entity';
import { ReqUser } from 'src/config/decorators';

@Controller('users')
export class AuthController {
  private readonly logger = new Logger('Auth');
  constructor(private authService: AuthService) {}

  @Post('login')
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async login(
    @Body('idToken') googleIdToken: string,
  ): Promise<{ accessToken: string }> {
    this.logger.log(`POST /users/login - idToken=${googleIdToken}`);
    const email: string = await this.authService.getGoogleEmail(googleIdToken);
    return await this.authService.login(email);
  }

  @Post('signup')
  @UsePipes(ValidationPipe)
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async signup(
    @Body() userCreateDto: UserCreateDto,
  ): Promise<{ accessToken: string; refreshToken: string }> {
    this.logger.log(`POST /users/signup - body=${userCreateDto}`);
    return this.authService.signup(userCreateDto);
  }

  @Post('refresh')
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async refreshTokens(
    @Body('refreshToken') refreshToken: string,
  ): Promise<{ accessToken: string; refreshToken: string }> {
    this.logger.log(`POST /users/refresh - refreshToken = ${refreshToken}`);
    return await this.authService.refreshTokens(refreshToken);
  }

  @Get('verify')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  verifyToken(@ReqUser() user: User): { userId: string } {
    this.logger.log(
      `GET /users/verify - nickname=${user.nickname} : response - userId=${user.user_id}`,
    );
    return { userId: user.user_id };
  }

  @Delete()
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async deleteUser(@ReqUser() user: User): Promise<{ userId: string }> {
    this.logger.log(`DELETE /users - nickname=${user.nickname}`);
    return await this.authService.deleteUser(user);
  }
}
