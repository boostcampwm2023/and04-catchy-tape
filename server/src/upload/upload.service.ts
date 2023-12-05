import { Inject, Injectable, LoggerService } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { NcloudConfigService } from './../config/ncloud.config';
import { S3 } from 'aws-sdk';
import { contentTypeHandler, keyFlags } from './../constants';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import * as fs from 'fs';
import { Readable } from 'stream';
import { Logger } from 'winston';

@Injectable()
export class UploadService {
  private objectStorage: S3;
  constructor(
    private readonly nCloudConfigService: NcloudConfigService,
    @Inject(Logger) private readonly logger: LoggerService,
  ) {
    this.objectStorage = nCloudConfigService.createObjectStorageOption();
  }

  private isValidType(flag: string): boolean {
    if (keyFlags.includes(flag)) return true;

    return false;
  }

  private isValidUUIDPattern(uuid: string): boolean {
    const uuidPattern =
      /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$/;

    if (uuidPattern.test(uuid)) return true;

    return false;
  }

  async uploadMusic(
    file: Express.Multer.File,
    musicId: string,
  ): Promise<{ url: string }> {
    try {
      if (!this.isValidUUIDPattern(musicId)) {
        this.logger.error(
          `upload.service - uploadMusic : INVALID_INPUT_UUID_VALUE`,
        );
        throw new CatchyException(
          'INVALID_INPUT_UUID_VALUE',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.INVALID_INPUT_UUID_VALUE,
        );
      }

      const uploadResult = await this.objectStorage
        .upload({
          Bucket: 'catchy-tape-bucket2',
          Key: `music/${musicId}/music.mp3`,
          Body: Readable.from(file.buffer),
          ContentType: contentTypeHandler.music,
          ACL: 'public-read',
        })
        .promise();

      return { url: uploadResult.Location };
    } catch {
      this.logger.error(`upload.service - uploadMusic : SERVICE_ERROR`);
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async uploadImage(
    file: Express.Multer.File,
    id: string,
    type: string,
  ): Promise<{ url: string }> {
    try {
      if (!this.isValidUUIDPattern(id)) {
        this.logger.error(
          `upload.service - uploadImage : INVALID_INPUT_UUID_VALUE`,
        );
        throw new CatchyException(
          'INVALID_INPUT_UUID_VALUE',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.INVALID_INPUT_UUID_VALUE,
        );
      }

      if (!this.isValidType(type)) {
        this.logger.error(
          `upload.service - uploadImage : INVALID_INPUT_TYPE_VALUE`,
        );
        throw new CatchyException(
          'INVALID_INPUT_TYPE_VALUE',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.INVALID_INPUT_TYPE_VALUE,
        );
      }

      const encodedFileName = encodeURIComponent(file.originalname);

      const keyPath =
        type === 'user'
          ? `image/user/${id}/image.png`
          : `image/cover/${id}/image.png`;

      const uploadResult = await this.objectStorage
        .upload({
          Bucket: 'catchy-tape-bucket2',
          Key: keyPath,
          Body: Readable.from(file.buffer),
          ContentType: contentTypeHandler.image,
          ACL: 'public-read',
        })
        .promise();

      return { url: uploadResult.Location };
    } catch {
      this.logger.error(`upload.service - uploadImage : SERVICE_ERROR`);
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
      this.logger.error(
        `upload.service - uploadEncodedFile : NCP_UPLOAD_ERROR`,
      );
      throw new CatchyException(
        'NCP_UPLOAD_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.ENCODED_MUSIC_UPLOAD_ERROR,
      );
    }
  }
}
