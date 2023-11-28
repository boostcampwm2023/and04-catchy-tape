import {
  Controller,
  Get,
  Req,
  Body,
  HttpCode,
  UseGuards,
  Post,
  UseInterceptors,
  UploadedFile,
  ParseFilePipe,
  FileTypeValidator,
  MaxFileSizeValidator,
  ValidationPipe,
  UsePipes,
} from '@nestjs/common';
import { UploadService } from './upload.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { AuthGuard } from '@nestjs/passport';
import { v4 } from 'uuid';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { FileInterceptor } from '@nestjs/platform-express';
import { fileSize } from 'src/constants';

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
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVER_ERROR,
      );
    }
  }

  @Post('/music')
  @UseGuards(AuthGuard())
  @UsePipes(ValidationPipe)
  @UseInterceptors(FileInterceptor('file'))
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async uploadMusic(
    @UploadedFile(
      new ParseFilePipe({
        validators: [
          new MaxFileSizeValidator({ maxSize: fileSize.MUSIC_SIZE }),
          new FileTypeValidator({ fileType: 'audio/mpeg' }),
        ],
      }),
    )
    file: Express.Multer.File,
    @Body('uuid') uuid: string | null,
  ) {
    const { url } = await this.uploadService.uploadMusic(file, uuid);
    return { url };
  }

  @Post('/image')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  @UseInterceptors(FileInterceptor('file'))
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async uploadImage(
    @Req() req,
    @UploadedFile(
      new ParseFilePipe({
        validators: [
          new MaxFileSizeValidator({ maxSize: fileSize.IMAGE_SIZE }),
          new FileTypeValidator({ fileType: 'image/png' }),
        ],
      }),
    )
    file: Express.Multer.File,
    @Body('type') type: string,
    @Body('uuid') uuid: string | null,
  ) {
    const userId = req.user.user_id;
    const id = type === 'user' ? userId : uuid;

    const { url } = await this.uploadService.uploadImage(file, id, type);
    return { url };
  }
}
