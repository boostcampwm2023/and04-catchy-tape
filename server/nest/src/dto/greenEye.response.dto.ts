import { IsNotEmpty, IsString } from 'class-validator';

export class GreenEyeResponseDto {
  @IsString()
  @IsNotEmpty()
  version: string;

  @IsString()
  @IsNotEmpty()
  requestId: string;

  @IsString()
  @IsNotEmpty()
  timestamp: number;

  @IsNotEmpty()
  images: [
    {
      result: {
        adult: {
          confidence: number;
        };
        normal: {
          confidence: number;
        };
        porn: {
          confidence: number;
        };
        sexy: {
          confidence: number;
        };
      };
      latency: number;
      confidence: number;
      message: string;
      name: string;
    },
  ];
}
