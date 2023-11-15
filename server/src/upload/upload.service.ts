import { HttpException, Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { NcloudConfigService } from './../config/ncloud.config';
import { S3 } from 'aws-sdk';
import { Readable } from 'stream';

@Injectable()
export class UploadService {
  private readonly objectStorage: S3;

  constructor(private readonly nCloudConfigService: NcloudConfigService) {
    this.objectStorage = nCloudConfigService.createObjectStorageOption();
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
      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
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
      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
    }
  }
}
