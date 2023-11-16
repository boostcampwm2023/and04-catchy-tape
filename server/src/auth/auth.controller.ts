import {
  Body,
  Controller,
  Get,
  HttpCode,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';

@Controller('users')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Get('login')
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async signup(
    @Body('idToken') googleIdToken: string,
  ): Promise<{ accessToken: string }> {
    return await this.authService.login(googleIdToken);
  }
}
