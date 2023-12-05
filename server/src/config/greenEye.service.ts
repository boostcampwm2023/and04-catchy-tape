import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { GreenEyeResponseDto } from 'src/dto/greenEye.response.dto';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';

@Injectable()
export class GreenEyeService {
  greenEyeSecretKey: string;
  greenEyeRequestUrl: string;

  contentType: string = 'application/json';

  constructor(private readonly configService: ConfigService) {
    this.greenEyeSecretKey = configService.get<string>('GREEN_EYE_SECRET_KEY');
    this.greenEyeRequestUrl = configService.get<string>(
      'GREEN_EYE_REQUEST_URL',
    );
  }

  private getTimeStamp(): number {
    return new Date().getTime();
  }

  private getRequestInit(
    imageUrl: string,
  ): Record<string, string | Record<string, string>> {
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
    try {
      return await fetch(this.greenEyeRequestUrl, this.getRequestInit(imageUrl))
        .then((res) => res.json())
        .then((err) => {
          throw new Error();
        });
    } catch {
      throw new CatchyException(
        'INVALID_GREEN_EYE_REQUEST',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.INVALID_GREEN_EYE_REQUEST,
      );
    }
  }
}
