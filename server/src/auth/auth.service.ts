import { HttpException, Injectable } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { InjectRepository } from '@nestjs/typeorm';
import { UserCreateDto } from 'src/dto/userCreate.dto';
import { User } from 'src/entity/user.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { Repository } from 'typeorm';
import { v4 as uuid } from 'uuid';

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
    private jwtService: JwtService,
  ) {}

  async login(email: string): Promise<{ accessToken: string }> {
    const user: User = await this.userRepository.findOneBy({
      user_email: email,
    });

    if (user) {
      const payload = { user_Id: user['user_id'] };
      const accessToken = this.jwtService.sign(payload);

      return { accessToken };
    } else {
      throw new HttpException(
        'NOT_EXIST_USER',
        HTTP_STATUS_CODE['WRONG_TOKEN'],
      );
    }
  }

  async signup(userCreateDto: UserCreateDto): Promise<{ accessToken: string }> {
    const { nickname, idToken } = userCreateDto;
    const email: string = await this.getGoogleEmail(idToken);

    if (await this.isExistEmail(email)) {
      throw new HttpException('EXIST_EMAIL', HTTP_STATUS_CODE.BAD_REQUEST);
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

      return this.login(email);
    } else {
      throw new HttpException('WRONG_TOKEN', HTTP_STATUS_CODE.WRONG_TOKEN);
    }
  }

  async getGoogleEmail(googleIdToken: string): Promise<string> {
    const googleApiUrl: string = `https://oauth2.googleapis.com/tokeninfo?id_token=${googleIdToken}`;

    const userInfo = await fetch(googleApiUrl, {
      method: 'GET',
    }).then((res) => res.json());

    if (!userInfo.email) {
      throw new HttpException('EXPIRED_TOKEN', HTTP_STATUS_CODE.WRONG_TOKEN);
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
}
