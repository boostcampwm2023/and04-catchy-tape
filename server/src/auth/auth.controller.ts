import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  Inject,
  Logger,
  LoggerService,
  Post,
  Req,
  UseGuards,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { UserCreateDto } from 'src/dto/userCreate.dto';
import { AuthGuard } from '@nestjs/passport';
import { User } from 'src/entity/user.entity';

@Controller('users')
export class AuthController {
  constructor(
    private authService: AuthService,
    @Inject(Logger) private readonly logger: LoggerService,
  ) {}

  @Post('login')
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async login(
    @Body('idToken') googleIdToken: string,
  ): Promise<{ accessToken: string }> {
    const email: string = await this.authService.getGoogleEmail(googleIdToken);
    return await this.authService.login(email);
  }

  @Post('signup')
  @UsePipes(ValidationPipe)
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async signup(
    @Body() userCreateDto: UserCreateDto,
  ): Promise<{ accessToken: string }> {
    return this.authService.signup(userCreateDto);
  }

  @Get('verify')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  verifyToken(@Req() req): { userId: string } {
    const user: User = req.user;
    this.logger.log(`GET /users/verify - ${user.nickname}: verified`);
    return { userId: user.user_id };
  }

  @Delete()
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async deleteUser(@Req() req): Promise<{ userId: string }> {
    const user: User = req.user;
    return await this.authService.deleteUser(user);
  }
}
