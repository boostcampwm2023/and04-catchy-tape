import {
  Controller,
  Get,
  Req,
  Query,
  HttpCode,
  UseGuards,
  HttpException,
} from '@nestjs/common';
import { UploadService } from './upload.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { AuthGuard } from '@nestjs/passport';
import { v4 } from 'uuid';

@Controller('upload')
export class UploadController {
  constructor(private uploadService: UploadService) {}

  @Get('uuid')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  getMusicUUID(): { uuid: string } {
    try {
      return { uuid: v4() };
    } catch (err) {
      console.log(err);
      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
    }
  }

  @Get()
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getSignedURL(
    @Query('type') type: string,
    @Query('uuid') uuid: string,
    @Req() req,
  ): Promise<{ signedUrl: string }> {
    try {
      const userId = req.user.user_id;
      const id = type === 'user' ? userId : uuid;

      const signedUrl = await this.uploadService.getSignedURL(type, id);

      return { signedUrl };
    } catch (error) {
      throw error;
    }
  }
}
