import {
  IsNotEmpty,
  IsOptional,
  IsString,
  Matches,
  MaxLength,
  MinLength,
} from 'class-validator';

export class UserUpdateDto {
  @IsOptional()
  @MinLength(2, { message: '닉네임이 2글자 미만입니다.' })
  @MaxLength(10, { message: '닉네임이 10글자 초과입니다.' })
  @Matches(/^[가-힣a-zA-Z0-9_.]+$/)
  nickname?: string;

  @IsNotEmpty()
  @IsString()
  image_url: string;
}
