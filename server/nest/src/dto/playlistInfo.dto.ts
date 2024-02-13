import { IsNotEmpty, IsNumber, IsString } from 'class-validator';

export class playlistInfo {
  @IsNotEmpty()
  @IsNumber()
  playlist_id: number;

  @IsNotEmpty()
  @IsString()
  playlist_title: string;

  @IsNotEmpty()
  @IsNumber()
  music_count: number = 0;

  @IsString()
  thumbnail: string | null;
}
