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
  Logger,
} from '@nestjs/common';
import { UploadService } from './upload.service';
import { HTTP_STATUS_CODE } from 'src/codes/httpStatusCode.enum';
import { AuthGuard } from '@nestjs/passport';
import { FileInterceptor } from '@nestjs/platform-express';
import { fileSize } from 'src/constants';
import { ReqUser } from 'src/config/decorators';
import { User } from 'src/entity/user.entity';

@Controller('upload')
export class UploadController {
  private readonly logger = new Logger('Upload');
  constructor(private uploadService: UploadService) {}

  @Get('uuid')
  @UseGuards(AuthGuard())
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  getMusicUUID(): { uuid: string } {
    this.logger.log(`GET /upload/uuid`);
    return { uuid: this.uploadService.getUUID() };
  }

  @Post('/music')
  @UseGuards(AuthGuard())
  @UsePipes(ValidationPipe)
  @UseInterceptors(FileInterceptor('file'))
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async uploadMusic(
    @ReqUser() user: User,
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
    this.logger.log(
      `POST /upload/music - nickname=${user.nickname}, uuid=${uuid}`,
    );
    const { url } = await this.uploadService.uploadMusic(file, uuid);
    return { url };
  }

  @Post('/image')
  @UseGuards(AuthGuard())
  @UsePipes(ValidationPipe)
  @UseInterceptors(FileInterceptor('file'))
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async uploadImage(
    @ReqUser() user: User,
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
      `POST /upload/image - nickname=${user.nickname}, type=${type}, uuid=${uuid}`,
    );

    const userId = user.user_id;
    const id = type === 'user' ? userId : uuid;

    const { url } = await this.uploadService.uploadImage(file, id, type);
    return { url };
  }
}
