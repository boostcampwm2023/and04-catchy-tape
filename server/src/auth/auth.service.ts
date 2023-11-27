import { Injectable } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { InjectRepository } from '@nestjs/typeorm';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { RECENT_PLAYLIST_NAME } from 'src/constants';
import { UserCreateDto } from 'src/dto/userCreate.dto';
import { User } from 'src/entity/user.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { PlaylistService } from 'src/playlist/playlist.service';
import { Repository } from 'typeorm';
import { v4 as uuid } from 'uuid';

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
    private jwtService: JwtService,
    private readonly playlistService: PlaylistService,
  ) {}

  async login(email: string): Promise<{ accessToken: string }> {
    const user: User = await this.userRepository.findOneBy({
      user_email: email,
    });

    if (user) {
      const payload = { user_id: user['user_id'] };
      const accessToken = this.jwtService.sign(payload);

      return { accessToken };
    } else {
      throw new CatchyException(
        'NOT_EXIST_USER',
        HTTP_STATUS_CODE['WRONG_TOKEN'],
        ERROR_CODE.NOT_EXIST_USER,
      );
    }
  }

  async signup(userCreateDto: UserCreateDto): Promise<{ accessToken: string }> {
    const { nickname, idToken } = userCreateDto;
    const email: string = await this.getGoogleEmail(idToken);

    if (await this.isExistEmail(email)) {
      throw new CatchyException(
        'ALREADY_EXIST_EMAIL',
        HTTP_STATUS_CODE.BAD_REQUEST,
        ERROR_CODE.ALREADY_EXIST_EMAIL,
      );
    }
    if (email) {
      const newUser: User = this.userRepository.create({
        user_id: uuid(),
        nickname,
        photo: null,
        user_email: email,
        created_at: new Date(),
      });
      await this.userRepository.save(newUser);

      this.playlistService.createPlaylist(newUser.user_id, {
        title: RECENT_PLAYLIST_NAME,
      });
      return this.login(email);
    }
    throw new CatchyException(
      'WRONG_TOKEN',
      HTTP_STATUS_CODE.WRONG_TOKEN,
      ERROR_CODE.WRONG_TOKEN,
    );
  }

  async getGoogleEmail(googleIdToken: string): Promise<string> {
    const googleApiUrl: string = `https://oauth2.googleapis.com/tokeninfo?id_token=${googleIdToken}`;

    const userInfo = await fetch(googleApiUrl, {
      method: 'GET',
    }).then((res) => res.json());

    if (!userInfo.email) {
      throw new CatchyException(
        'EXPIRED_TOKEN',
        HTTP_STATUS_CODE.WRONG_TOKEN,
        ERROR_CODE.EXPIRED_TOKEN,
      );
    }
    return userInfo.email;
  }

  async isExistEmail(email: string): Promise<boolean> {
    const user: User = await this.userRepository.findOneBy({
      user_email: email,
    });

    if (!user) {
      return false;
    } else {
      return true;
    }
  }

  async deleteUser(user: User): Promise<{ userId: string }> {
    await this.userRepository.delete(user.user_id);
    return { userId: user.user_id };
  }
}
