import {
  Body,
  Controller,
  Get,
  HttpCode,
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

  @Get('verify')
  @UseGuards(AuthGuard())
  verifyToken(@Req() req): { userId: string } {
    const user = req.user;
    return { userId: user.user_id };
  }
}
