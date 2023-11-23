import {
  Post,
  Controller,
  Get,
  Req,
  Query,
  HttpCode,
  UseGuards,
  UseInterceptors,
  UploadedFile,
  ParseFilePipe,
  FileTypeValidator,
} from '@nestjs/common';
import { UploadService } from './upload.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { AuthGuard } from '@nestjs/passport';
import { v4 } from 'uuid';
import { FileInterceptor } from '@nestjs/platform-express';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';

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
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVER_ERROR,
      );
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

  @Post('/music')
  @UseInterceptors(FileInterceptor('file'))
  async uploadMusic(
    @UploadedFile(
      new ParseFilePipe({
        validators: [
          // new MaxFileSizeValidator({ maxSize: fileSize.MUSIC_FILE_LIMIT_SIZE }),
          new FileTypeValidator({ fileType: 'audio/mpeg' }),
        ],
      }),
    )
    file: Express.Multer.File,
  ) {
    const { url } = await this.uploadService.uploadMusic(file);
    return { url };
  }

  @Post('/image')
  @UseInterceptors(FileInterceptor('file'))
  async uploadImage(
    @UploadedFile(
      new ParseFilePipe({
        validators: [
          // new MaxFileSizeValidator({ maxSize: fileSize.IMAGE_FILE_LIMIT_SIZE }),
          new FileTypeValidator({ fileType: 'image/png' }),
        ],
      }),
    )
    file: Express.Multer.File,
  ) {
    const { url } = await this.uploadService.uploadImage(file);
    return { url };
  }
}
