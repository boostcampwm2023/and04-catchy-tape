import { HttpException, Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { NcloudConfigService } from './../config/ncloud.config';
import { S3 } from 'aws-sdk';
import { keyFlags, keyHandler } from './../constants';

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

  private isValidUUIDPattern(uuid: string): boolean {
    const uuidPattern =
      /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$/;

    if (uuidPattern.test(uuid)) return true;

    return false;
  }

  async getSignedURL(type: string, uuid: string): Promise<string> {
    try {
      if (!this.isValidUUIDPattern(uuid) || !this.isValidFlag(type))
        throw new HttpException(
          'INVALID_INPUT_VALUE',
          HTTP_STATUS_CODE.BAD_REQUEST,
        );

      const keyPath = keyHandler[type](uuid);

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
