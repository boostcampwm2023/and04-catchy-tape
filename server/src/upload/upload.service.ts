import { HttpException, Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { NcloudConfigService } from './../config/ncloud.config';
import { S3 } from 'aws-sdk';
import { fileSize, keyFlags, keyHandler } from './../constants';

@Injectable()
export class UploadService {
  private objectStorage: S3;
  constructor(private readonly nCloudConfigService: NcloudConfigService) {
    this.objectStorage = nCloudConfigService.createObjectStorageOption();
  }

  private isValidFlag(flag: string): boolean {
    if (keyFlags.includes(flag)) return true;

    return false;
  }

  async getSignedURL(flag: string, uuid: string): Promise<string> {
    try {
      if (!this.isValidFlag(flag))
        throw new HttpException('BAD_REQUEST', HTTP_STATUS_CODE.BAD_REQUEST);

      const keyPath = keyHandler[flag](uuid);

      return await this.objectStorage.getSignedUrlPromise('putObject', {
        Bucket: 'catchy-tape-bucket2',
        Key: `${keyPath}`,
        Expires: 600,
        ACL: 'public-read',
      });
    } catch (error) {
      if (error instanceof HttpException) throw error;

      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
    }
  }
}
