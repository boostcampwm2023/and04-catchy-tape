import { HttpException, Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { User } from 'src/entity/user.entity';
import { Music } from 'src/entity/music.entity';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { PlaylistService } from 'src/playlist/playlist.service';

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
    private playlistService: PlaylistService,
  ) {}

  async isDuplicatedUserEmail(userNickname: string): Promise<boolean> {
    try {
      const user = await this.userRepository.findOneBy({
        nickname: userNickname,
      });

      if (user) {
        return true;
      }

      return false;
    } catch {
      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
    }
  }

  async getRecentPlayedMusicByUserId(userId: string): Promise<Music[]> {
    return await this.playlistService.getRecentMusicsByUserId(userId);
  }
}
