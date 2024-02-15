import axios from 'axios';
import * as fs from 'fs';
import { Injectable, Logger } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/codes/httpStatusCode.enum';
import { NcloudConfigService } from '../config/ncloud.config';
import { S3 } from 'aws-sdk';
import { contentTypeHandler, keyFlags } from '../constants';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/codes/errorCode.enum';
import { Readable } from 'stream';
import { GreenEyeService } from '../config/greenEye.config';
import { DeleteObjectOutput } from 'aws-sdk/clients/s3';
import { CloudFunctionsResponseDto } from 'src/dto/cloudFunctions.response.dto';
import { v4 } from 'uuid';

@Injectable()
export class UploadService {
  private readonly logger = new Logger('UploadService');
  private objectStorage: S3;
  private encodingActionUrl: string;
  constructor(
    private readonly nCloudConfigService: NcloudConfigService,
    private readonly greenEyeService: GreenEyeService,
  ) {
    this.objectStorage = nCloudConfigService.createObjectStorageOption();
    this.encodingActionUrl = nCloudConfigService.getExecuteActionsUrl();
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

  private async deleteObjectStorageImage(
    path: string,
  ): Promise<DeleteObjectOutput> {
    return await this.objectStorage
      .deleteObject({
        Bucket: 'catchy-tape-bucket2',
        Key: path,
      })
      .promise();
  }

  getUUID(): string {
    try {
      return v4();
    } catch {
      this.logger.error(`upload.controller - getMusicUUID : SERVER_ERROR`);
      throw new CatchyException(
        'SERVER ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVER_ERROR,
      );
    }
  }

  async checkImageNormal(
    message: string,
    confidence: number,
    keyPath: string,
  ): Promise<void> {
    if (message !== 'SUCCESS') {
      await this.deleteObjectStorageImage(keyPath);

      this.logger.error(
        `upload.service - checkImageNormal : FAIL_GREEN_EYE_IMAGE_RECOGNITION`,
      );
      throw new CatchyException(
        'FAIL_GREEN_EYE_IMAGE_RECOGNITION',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.FAIL_GREEN_EYE_IMAGE_RECOGNITION,
      );
    }

    if (confidence < 0.7) {
      await this.deleteObjectStorageImage(keyPath);

      this.logger.error(`upload.service - checkImageNormal : BAD_IMAGE`);
      throw new CatchyException(
        'BAD_IMAGE',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.BAD_IMAGE,
      );
    }
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

      await this.objectStorage
        .upload({
          Bucket: 'catchy-tape-bucket2',
          Key: `music/${musicId}/music.mp3`,
          Body: Readable.from(file.buffer),
          ContentType: contentTypeHandler.music,
          ACL: 'public-read',
        })
        .promise();

      const params = {
        container_name: 'catchy-tape-bucket2',
        music_id: musicId,
      };

      const result: CloudFunctionsResponseDto = await axios
        .post(
          this.encodingActionUrl,
          params,
          this.nCloudConfigService.getRequestActionUrlHeaders(),
        )
        .then((response) => response.data)
        .catch((err) => {
          this.logger.error(
            `upload.service - uploadMusic : MUSIC_ENCODE_REQUEST_ERROR`,
          );
          throw new CatchyException(
            'MUSIC_ENCODE_ERROR',
            HTTP_STATUS_CODE.SERVER_ERROR,
            ERROR_CODE.MUSIC_ENCODE_ERROR,
          );
        });

      if (!result.body.url) {
        this.logger.error(`upload.service - uploadMusic : MUSIC_ENCODE_ERROR`);
        throw new CatchyException(
          'MUSIC_ENCODE_ERROR',
          HTTP_STATUS_CODE.SERVER_ERROR,
          ERROR_CODE.MUSIC_ENCODE_ERROR,
        );
      }

      return { url: result.body.url };
    } catch (err) {
      console.log(err);
      if (err instanceof CatchyException) {
        throw err;
      }

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
    id: string | null,
    type: string,
  ): Promise<{ url: string }> {
    try {
      if (!id) {
        this.logger.error(
          `upload.controller - uploadImage : NOT_EXIST_MUSIC_ID`,
        );
        throw new CatchyException(
          'NOT_EXIST_MUSIC_ID',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.NOT_EXIST_MUSIC_ID,
        );
      }

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

      const { images } = await this.greenEyeService.getResultOfNormalImage(
        uploadResult.Location,
      );

      await this.checkImageNormal(
        images[0].message,
        images[0].confidence,
        keyPath,
      );

      return { url: uploadResult.Location };
    } catch (err) {
      if (err instanceof CatchyException) {
        throw err;
      }

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
