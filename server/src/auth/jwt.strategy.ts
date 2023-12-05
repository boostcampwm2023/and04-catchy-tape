import { Inject, Injectable, LoggerService } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { PassportStrategy } from '@nestjs/passport';
import { InjectRepository } from '@nestjs/typeorm';
import { Strategy, ExtractJwt } from 'passport-jwt';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/config/errorCode.enum';
import { User } from 'src/entity/user.entity';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { Repository } from 'typeorm';
import { Logger } from 'winston';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
    private configService: ConfigService,
    @Inject(Logger) private readonly logger: LoggerService,
  ) {
    super({
      secretOrKey: configService.get<string>('JWT_SECRET_KEY'),
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
    });
  }

  async validate(payload): Promise<User> {
    const { user_id } = payload;

    const user: User = await this.userRepository.findOne({
      where: { user_id },
    });

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
