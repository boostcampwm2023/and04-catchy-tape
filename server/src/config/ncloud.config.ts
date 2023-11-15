import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as AWS from 'aws-sdk';

@Injectable()
export class NcloudConfigService {
  constructor(private readonly configService: ConfigService) {}

  createObjectStorageOption() {
    return new AWS.S3({
      endpoint: 'https://kr.object.ncloudstorage.com',
      region: 'kr-standard',
      credentials: {
        accessKeyId: this.configService.get<string>('ACCESS_ID'),
        secretAccessKey: this.configService.get<string>('SECRET_ACCESS_KEY'),
      },
    });
  }
}
