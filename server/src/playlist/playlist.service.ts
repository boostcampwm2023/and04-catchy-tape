import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { PlaylistCreateDto } from 'src/dto/playlistCreate.dto';
import { Playlist } from 'src/entity/playlist.entity';
import { Repository } from 'typeorm';

@Injectable()
export class PlaylistService {
  constructor(
    @InjectRepository(Playlist)
    private playlistRepository: Repository<Playlist>,
  ) {}

  async createPlaylist(userId: string, playlistCreateDto: PlaylistCreateDto): Promise<number> {
    const title: string = playlistCreateDto.title;
    const newPlaylist: Playlist = this.playlistRepository.create({
      playlist_title: title,
      created_at: new Date(),
      updated_at: new Date(),
      user: { user_id: userId },
    });

    const result: Playlist = await this.playlistRepository.save(newPlaylist);
    const playlistId: number = parseInt(result.playlistId);
    return playlistId;
  }
}
