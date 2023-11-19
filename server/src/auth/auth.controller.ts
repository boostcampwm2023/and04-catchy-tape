import {
  Body,
  Controller,
  HttpCode,
  Post,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { UserCreateDto } from 'src/dto/userCreate.dto';

@Controller('users')
export class AuthController {
  constructor(private authService: AuthService) {}

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
}
