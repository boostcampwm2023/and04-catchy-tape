import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as AWS from 'aws-sdk';
import CryptoJS from 'crypto-js';
import { getTimeStamp } from 'src/constants';

@Injectable()
export class NcloudConfigService {
  private readonly accessKey: string;
  private readonly secretKey: string;
  private readonly gatewayKey: string;
  private readonly executeActionsUrl: string;
  private readonly requestActionsUrl: string;
  private readonly timestamp: string;

  constructor(private readonly configService: ConfigService) {
    this.accessKey = this.configService.get<string>('ACCESS_ID');
    this.secretKey = this.configService.get<string>('SECRET_ACCESS_KEY');
    this.gatewayKey = this.configService.get<string>('API_GW_ACCESS_KEY');
    this.executeActionsUrl = this.configService.get<string>(
      'CLOUD_FUNCTIONS_EXECUTE_URL',
    );
    this.requestActionsUrl = this.configService.get<string>(
      'CLOUD_FUNCTIONS_REQUEST_URL',
    );
    this.timestamp = `${getTimeStamp()}`;
  }

  createObjectStorageOption(): AWS.S3 {
    return new AWS.S3({
      endpoint: 'https://kr.object.ncloudstorage.com',
      region: 'kr-standard',
      credentials: {
        accessKeyId: this.accessKey,
        secretAccessKey: this.secretKey,
      },
      signatureVersion: 'v4',
    });
  }

  makeSignature(): string {
    const hmac = CryptoJS.algo.HMAC.create(
      CryptoJS.algo.SHA256,
      this.secretKey,
    );

    hmac.update('POST');
    hmac.update(' ');
    hmac.update(this.requestActionsUrl);
    hmac.update('\n');
    hmac.update(this.timestamp);
    hmac.update('\n');
    hmac.update(this.accessKey);

    const hash = hmac.finalize();

    return hash.toString(CryptoJS.enc.Base64);
  }

  getExecuteActionsUrl(): string {
    return this.executeActionsUrl;
  }

  getRequestActionUrlHeaders(): { headers: Record<string, string> } {
    return {
      headers: {
        'Content-Type': 'application/json',
        'x-ncp-iam-access-key': this.accessKey,
        'x-ncp-apigw-timestamp': this.timestamp,
        'x-ncp-apigw-api-key': this.gatewayKey,
        'x-ncp-apigw-signature-v2': this.makeSignature(),
      },
    };
  }
}
