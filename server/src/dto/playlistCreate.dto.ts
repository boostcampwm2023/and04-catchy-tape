import { IsNotEmpty, IsString, Matches, MaxLength } from "class-validator";

export class PlaylistCreateDto {
    @IsNotEmpty()
    @IsString()
    @MaxLength(20, { message: '글자 수가 50을 넘어갔습니다.' })
    @Matches(/^[가-힣a-zA-Z ]+$/)
    title: string;
  }