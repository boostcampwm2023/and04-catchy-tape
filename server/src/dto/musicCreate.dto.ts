import { IsNotEmpty, IsString, MaxLength } from 'class-validator';
import { Genres } from 'src/constants';

export class MusicCreateDto {
  @IsNotEmpty()
  @IsString()
  @MaxLength(50, { message: '글자 수가 50을 넘어갔습니다.' })
  title: string;

  @IsNotEmpty()
  @IsString()
  cover: string;

  @IsNotEmpty()
  @IsString()
  file: string;

  @IsNotEmpty()
  @IsString()
  genre: Genres;
}
