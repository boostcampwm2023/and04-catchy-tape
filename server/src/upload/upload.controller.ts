import {
  Controller,
  Post,
  UploadedFile,
  UseInterceptors,
  ParseFilePipe,
  MaxFileSizeValidator,
  FileTypeValidator,
} from '@nestjs/common';
import { UploadService } from './upload.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { FileInterceptor } from '@nestjs/platform-express';
import { fileSize } from 'src/constants';

@Controller('upload')
export class UploadController {
  constructor(private uploadService: UploadService) {}

  @Post('/music')
  @UseInterceptors(FileInterceptor('file'))
  async uploadMusic(
    @UploadedFile(
      new ParseFilePipe({
        validators: [
          new MaxFileSizeValidator({ maxSize: fileSize.MUSIC_FILE_LIMIT_SIZE }),
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
          new MaxFileSizeValidator({ maxSize: fileSize.IMAGE_FILE_LIMIT_SIZE }),
          new FileTypeValidator({ fileType: 'image/jpeg' }),
        ],
      }),
    )
    file: Express.Multer.File,
  ) {
    const { url } = await this.uploadService.uploadImage(file);
    return { url };
  }
}
