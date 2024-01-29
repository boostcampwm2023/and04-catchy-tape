import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as AWS from 'aws-sdk';
import { HmacSHA256, enc } from 'crypto-js';
import { getTimeStamp } from 'src/constants';

@Injectable()
export class NcloudConfigService {
  private readonly accessKey: string;
  private readonly secretKey: string;
  private readonly gatewayKey: string;
  private readonly executeActionsUrl: string;
  private readonly requestActionsUrl: string;

  constructor(private readonly configService: ConfigService) {
    this.accessKey = this.configService.get<string>('ACCESS_ID', '') ;
    this.secretKey = this.configService.get<string>('SECRET_ACCESS_KEY', '') ;
    this.gatewayKey = this.configService.get<string>('API_GW_ACCESS_KEY', '') ;
    this.executeActionsUrl = this.configService.get<string>(
      'CLOUD_FUNCTIONS_EXECUTE_URL',''
    );
    this.requestActionsUrl = this.configService.get<string>(
      'CLOUD_FUNCTIONS_REQUEST_URL',''
    );
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

  makeSignature(curTimeStamp: number): string {
    const method = 'POST';
    const blank = ' ';
    const url = this.requestActionsUrl;
    const line = '\n';
    const timestamp = `${curTimeStamp}`;
    const accessKey = this.accessKey;

    const message = `${method}${blank}${url}${line}${timestamp}${line}${accessKey}`;

    const hmac = HmacSHA256(message, this.secretKey);

    return enc.Base64.stringify(hmac);
  }

  getExecuteActionsUrl(): string {
    return this.executeActionsUrl;
  }

  getRequestActionUrlHeaders(): { headers: Record<string, string> } {
    const curTimeStamp = getTimeStamp();
    return {
      headers: {
        'Content-Type': 'application/json',
        'x-ncp-iam-access-key': this.accessKey,
        'x-ncp-apigw-timestamp': `${curTimeStamp}`,
        'x-ncp-apigw-api-key': this.gatewayKey,
        'x-ncp-apigw-signature-v2': this.makeSignature(curTimeStamp),
      },
    };
  }
}
