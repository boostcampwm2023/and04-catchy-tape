import { Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { NcloudConfigService } from './../config/ncloud.config';
import { S3 } from 'aws-sdk';
import { keyFlags, keyHandler } from './../constants';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import * as fs from 'fs';
import { Readable } from 'stream';

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
        throw new CatchyException(
          'INVALID_INPUT_VALUE',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.INVALID_INPUT_VALUE,
        );

      const keyPath = keyHandler[type](uuid);

      return await this.objectStorage.getSignedUrlPromise('putObject', {
        Bucket: 'catchy-tape-bucket2',
        Key: `${keyPath}`,
        Expires: 600,
        ACL: 'public-read',
      });
    } catch (error) {
      if (error instanceof CatchyException) throw error;

      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async uploadMusic(file: Express.Multer.File): Promise<{ url: string }> {
    try {
      const uploadResult = await this.objectStorage
        .upload({
          Bucket: 'catchy-tape-bucket2',
          Key: `music/original/${file.originalname}`,
          Body: Readable.from(file.buffer),
        })
        .promise();

      return { url: uploadResult.Location };
    } catch {
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async uploadImage(file: Express.Multer.File): Promise<{ url: string }> {
    try {
      const uploadResult = await this.objectStorage
        .upload({
          Bucket: 'catchy-tape-bucket2',
          Key: `image/${file.originalname}`,
          Body: Readable.from(file.buffer),
        })
        .promise();

      return { url: uploadResult.Location };
    } catch {
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async uploadEncodedFile(
    filePath: string,
    musicId: string,
    fileName: string,
  ): Promise<{ url: string }> {
    try {
      const uploadResult = await this.objectStorage
        .upload({
          Bucket: 'catchy-tape-bucket2',
          Key: `music/${musicId}/${fileName}`,
          Body: fs.createReadStream(filePath),
          ACL: 'public-read',
        })
        .promise();

      return { url: uploadResult.Location };
    } catch (err) {
      console.log(err);
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }
}
