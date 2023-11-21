import { Controller, Get, Query, HttpCode } from '@nestjs/common';
import { UploadService } from './upload.service';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';

@Controller('upload')
export class UploadController {
  constructor(private uploadService: UploadService) {}

  @Get()
  @HttpCode(HTTP_STATUS_CODE.SUCCESS)
  async getSignedURL(
    @Query('path') path: string,
    @Query('fileName') fileName: string,
    @Query('ext') ext: string,
  ): Promise<{ signedUrl: string }> {
    try {
      const signedUrl = await this.uploadService.getSignedURL(
        path,
        fileName,
        ext,
      );

      return { signedUrl };
    } catch (error) {
      throw error;
    }
  }
}
