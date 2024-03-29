import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/codes/errorCode.enum';
import { GreenEyeResponseDto } from 'src/dto/greenEye.response.dto';
import { HTTP_STATUS_CODE } from 'src/codes/httpStatusCode.enum';
import { Logger } from '@nestjs/common';

@Injectable()
export class GreenEyeService {
  private readonly logger = new Logger('GreenEyeService');
  greenEyeSecretKey: string;
  greenEyeRequestUrl: string;

  contentType: string = 'application/json';

  constructor(private readonly configService: ConfigService) {
    this.greenEyeSecretKey = configService.get<string>(
      'GREEN_EYE_SECRET_KEY',
      '',
    );
    this.greenEyeRequestUrl = configService.get<string>(
      'GREEN_EYE_REQUEST_URL',
      '',
    );
  }

  private getTimeStamp(): number {
    return new Date().getTime();
  }

  private getRequestInit(imageUrl: string): RequestInit {
    return {
      method: 'POST',
      headers: {
        'X-GREEN-EYE-SECRET': this.greenEyeSecretKey,
        'Content-Type': this.contentType,
      },
      body: JSON.stringify({
        version: 'V1',
        requestId: 'requestId',
        timestamp: this.getTimeStamp(),
        images: [
          {
            name: 'image',
            url: imageUrl,
          },
        ],
      }),
    };
  }

  async getResultOfNormalImage(imageUrl: string): Promise<GreenEyeResponseDto> {
    return await fetch(this.greenEyeRequestUrl, this.getRequestInit(imageUrl))
      .then((res) => {
        if (res.ok) {
          return res.json();
        }

        throw new Error();
      })
      .catch((err) => {
        this.logger.error(
          `greenEye.service - getResultOfNormalImage : INVALID_GREEN_EYE_REQUEST`,
        );
        throw new CatchyException(
          'INVALID_GREEN_EYE_REQUEST',
          HTTP_STATUS_CODE.BAD_REQUEST,
          ERROR_CODE.INVALID_GREEN_EYE_REQUEST,
        );
      });
  }
}
