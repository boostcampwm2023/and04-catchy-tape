import { IsNotEmpty, IsString, Matches, MaxLength } from 'class-validator';
import { Genres } from 'src/constants';

export class MusicCreateDto {
  @IsNotEmpty()
  @IsString()
  @Matches(/^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$/)
  music_id: string;

  @IsNotEmpty()
  @IsString()
  @MaxLength(50, { message: '글자 수가 50을 넘어갔습니다.' })
  @Matches(/[\w]+|[가-힣]+/)
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
