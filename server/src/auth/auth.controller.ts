import {
  Body,
  Controller,
  HttpCode,
  Post,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';

@Controller('users')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Post('login')
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async signup(
    @Body('idToken') googleIdToken: string,
  ): Promise<{ accessToken: string }> {
    return await this.authService.login(googleIdToken);
  }
}
