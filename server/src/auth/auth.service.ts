import { HttpException, Injectable } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from 'src/entity/user.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { Repository } from 'typeorm';

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
    private jwtService: JwtService,
  ) {}

  async login(googleIdToken: string): Promise<{ accessToken: string }> {
    const email: string = await this.getGoogleEmail(googleIdToken);
    const user: User = await this.userRepository.findOneBy({
      user_email: email,
    });

    if (user) {
      const payload = { userId: user['userId'] };
      const accessToken = this.jwtService.sign(payload);

      return { accessToken };
    } else {
      throw new HttpException(
        'NOT_EXIST_USER',
        HTTP_STATUS_CODE['WRONG TOKEN'],
      );
    }
  }

  async getGoogleEmail(googleIdToken: string): Promise<string> {
    const googleApiUrl: string = `https://oauth2.googleapis.com/tokeninfo?id_token=${googleIdToken}`;

    const userInfo = await fetch(googleApiUrl, {
      method: 'GET',
    }).then((res) => res.json());

    return userInfo.email;
  }
}
