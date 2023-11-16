import { IsNotEmpty, IsString } from 'class-validator';

export class MusicCreateDto {
  @IsNotEmpty()
  @IsString()
  title: string;

  @IsNotEmpty()
  @IsString()
  cover: string;

  @IsNotEmpty()
  @IsString()
  file: string;
}
