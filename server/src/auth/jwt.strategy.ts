import { Logger, Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { PassportStrategy } from '@nestjs/passport';
import { Strategy, ExtractJwt } from 'passport-jwt';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/codes/errorCode.enum';
import { User } from 'src/entity/user.entity';
import { HTTP_STATUS_CODE } from 'src/codes/httpStatusCode.enum';
import { UserRepository } from 'src/repository/user.repository';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  private readonly logger = new Logger('JwtLogger');
  constructor(
    private userRepository: UserRepository,
    private configService: ConfigService,
  ) {
    super({
      secretOrKey: configService.get<string>('JWT_SECRET_KEY'),
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
    });
  }

  async validate(payload: { user_id: string }): Promise<User> {
    const { user_id } = payload;

    const user = await this.userRepository.findUserById(user_id);

    if (!user || !user_id) {
      this.logger.error(`request user_id=${user_id} : NOT_EXIST_USER`);
      throw new CatchyException(
        'NOT_EXIST_USER',
        HTTP_STATUS_CODE['WRONG_TOKEN'],
        ERROR_CODE.NOT_EXIST_USER,
      );
    }

    return user;
  }
}
