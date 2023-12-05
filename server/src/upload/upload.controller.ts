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
  Inject,
  LoggerService,
} from '@nestjs/common';
import { UploadService } from './upload.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { AuthGuard } from '@nestjs/passport';
import { v4 } from 'uuid';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { FileInterceptor } from '@nestjs/platform-express';
import { fileSize } from 'src/constants';
import { Logger } from 'winston';

@Controller('upload')
export class UploadController {
  constructor(
    private uploadService: UploadService,
    @Inject(Logger) private readonly logger: LoggerService,
  ) {}

  @Get('uuid')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  getMusicUUID(): { uuid: string } {
    try {
      this.logger.log(`GET /upload/uuid`);
      return { uuid: v4() };
    } catch (err) {
      this.logger.error(`upload.controller - getMusicUUID : SERVER_ERROR`);
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
    @Body('uuid') uuid: string,
  ): Promise<{ url: string }> {
    this.logger.log(`POST /upload/music - uuid=${uuid}`);
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
  ): Promise<{ url: string }> {
    this.logger.log(
      `POST /upload/image - nickname=${req.user.nickname}, type=${type}, uuid=${uuid}`,
    );
    try {
      const userId = req.user.user_id;
      const id = type === 'user' ? userId : uuid;

      if (!id) {
        throw new Error();
      }

      const { url } = await this.uploadService.uploadImage(file, id, type);
      return { url };
    } catch (err) {
      if (err instanceof CatchyException) throw err;

      this.logger.error(`upload.controller - uploadImage : NOT_EXIST_MUSIC_ID`);
      throw new CatchyException(
        'NOT_EXIST_MUSIC_ID',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.NOT_EXIST_MUSIC_ID,
      );
    }
  }
}
