import { HttpException, Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { NcloudConfigService } from './../config/ncloud.config';
import { S3 } from 'aws-sdk';
import { fileExt } from './../constants';

@Injectable()
export class UploadService {
  private objectStorage: S3;
  constructor(private readonly nCloudConfigService: NcloudConfigService) {
    this.objectStorage = nCloudConfigService.createObjectStorageOption();
  }

  private isValidPath(path: string) {
    if (path.startsWith('image/user/')) return true;
    if (path.startsWith('image/cover/')) return true;
    if (path.startsWith('music/')) return true;

    return false;
  }

  private isValidExt(ext: string) {
    if (fileExt.includes(ext)) return true;

    return false;
  }

  async getSignedURL(
    keyPath: string,
    fileName: string,
    ext: string,
  ): Promise<string> {
    try {
      if (!this.isValidPath(keyPath) || !this.isValidExt(ext))
        throw new HttpException('BAD REQUEST', HTTP_STATUS_CODE.BAD_REQUEST);

      const url = await this.objectStorage.getSignedUrlPromise('putObject', {
        Bucket: 'catchy-tape-bucket2',
        Key: `${keyPath}/${fileName}.${ext}`,
        Expires: 60,
      });

      return url;
    } catch (error) {
      if (error instanceof HttpException) throw error;

      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
    }
  }
}
