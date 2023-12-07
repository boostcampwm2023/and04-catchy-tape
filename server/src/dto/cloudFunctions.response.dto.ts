import { IsNotEmpty } from 'class-validator';

export class CloudFunctionsResponseDto {
  body: {
    url: string;
  };

  @IsNotEmpty()
  headers: {
    'Content-Type': string;
  };

  @IsNotEmpty()
  statusCode: number;
}
