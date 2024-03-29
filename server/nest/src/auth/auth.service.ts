import { CACHE_MANAGER } from '@nestjs/cache-manager';
import { Cache } from 'cache-manager';
import { Injectable, Logger, Inject } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { JwtService } from '@nestjs/jwt';
import { InjectRepository } from '@nestjs/typeorm';
import { CatchyException } from 'src/config/catchyException';
import { ERROR_CODE } from 'src/codes/errorCode.enum';
import { UserCreateDto } from 'src/dto/userCreate.dto';
import { User } from 'src/entity/user.entity';
import { HTTP_STATUS_CODE } from 'src/codes/httpStatusCode.enum';
import { Repository, DataSource } from 'typeorm';
import { v4 } from 'uuid';
import { ONE_WEEK_TO_SECONDS } from 'src/constants';
import { UserRepository } from 'src/repository/user.repository';

@Injectable()
export class AuthService {
  private readonly logger = new Logger('AuthService');
  private readonly refreshOptions: {};

  constructor(
    @Inject(CACHE_MANAGER) private cacheManager: Cache,
    private jwtService: JwtService,
    private readonly dataSource: DataSource,
    private readonly configService: ConfigService,
    private userRepository: UserRepository,
  ) {
    this.refreshOptions = {
      secret: this.configService.get<string>('REFRESH_SECRET_KEY'),
    };
  }

  async login(
    email: string,
  ): Promise<{ accessToken: string; refreshToken: string }> {
    try {
      const user = await this.userRepository.findUserByEmail(email);

      if (user) {
        const accessPayload = { user_id: user.user_id };

        const refreshUuid = v4();
        const refreshPayload = { refresh_id: refreshUuid };
        await this.cacheManager.set(refreshUuid, user.user_id, {
          ttl: ONE_WEEK_TO_SECONDS,
        });

        const accessToken = this.jwtService.sign(accessPayload);
        const refreshToken = this.jwtService.sign(
          refreshPayload,
          this.refreshOptions,
        );

        return { accessToken, refreshToken };
      }

      this.logger.error(`auth.service - login : NOT_EXIST_USER`);
      throw new CatchyException(
        'NOT_EXIST_USER',
        HTTP_STATUS_CODE['WRONG_TOKEN'],
        ERROR_CODE.NOT_EXIST_USER,
      );
    } catch (error) {
      if (error instanceof CatchyException) {
        throw error;
      }

      throw new CatchyException(
        'SERVICE_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async signup(
    userCreateDto: UserCreateDto,
  ): Promise<{ accessToken: string; refreshToken: string }> {
    const { nickname, idToken } = userCreateDto;
    const email: string = await this.getGoogleEmail(idToken);

    if (await this.isExistEmail(email)) {
      this.logger.error(`auth.service - signup : ALREADY_EXIST_EMAIL`);
      throw new CatchyException(
        'ALREADY_EXIST_EMAIL',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.ALREADY_EXIST_EMAIL,
      );
    }

    try {
      await this.userRepository.saveUser(nickname, email);

      return await this.login(email);
    } catch (error) {
      if (error instanceof CatchyException) {
        throw error;
      }

      throw new CatchyException(
        'SERVICE_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async refreshTokens(
    refreshToken: string,
  ): Promise<{ accessToken: string; refreshToken: string }> {
    try {
      const { refresh_id } = this.jwtService.decode(
        refreshToken,
        this.refreshOptions,
      );

      const user_id: string | undefined =
        await this.cacheManager.get(refresh_id);

      if (!user_id) {
        this.logger.error(
          `auth.service - refreshTokens : NOT_EXIST_REFRESH_TOKEN`,
        );
        throw new CatchyException(
          'NOT_EXIST_REFRESH_TOKEN',
          HTTP_STATUS_CODE.WRONG_TOKEN,
          ERROR_CODE.NOT_EXIST_REFRESH_TOKEN,
        );
      }

      if ((await this.userRepository.countNumberOfUserById(user_id)) == 0) {
        this.logger.error(`auth.service - refreshTokens : NOT_EXIST_USER`);
        throw new CatchyException(
          'NOT_EXIST_USER',
          HTTP_STATUS_CODE.WRONG_TOKEN,
          ERROR_CODE.NOT_EXIST_USER,
        );
      }

      const payload = { user_id };
      const newRefreshTokenUuid = v4();
      const newAccessToken = this.jwtService.sign(payload);
      const newRefreshToken = this.jwtService.sign(
        { refresh_id: newRefreshTokenUuid },
        this.refreshOptions,
      );

      await this.cacheManager.del(refresh_id);
      await this.cacheManager.set(newRefreshTokenUuid, user_id, {
        ttl: ONE_WEEK_TO_SECONDS,
      });

      return { accessToken: newAccessToken, refreshToken: newRefreshToken };
    } catch (error) {
      if (error instanceof CatchyException) {
        throw error;
      }
      this.logger.error(`auth.service - refreshTokens : SERVICE_ERROR`);
      console.log(error);

      throw new CatchyException(
        'SERVICE_ERROR',
        HTTP_STATUS_CODE.SERVER_ERROR,
        ERROR_CODE.SERVICE_ERROR,
      );
    }
  }

  async getGoogleEmail(googleIdToken: string): Promise<string> {
    const googleApiUrl: string = `https://oauth2.googleapis.com/tokeninfo?id_token=${googleIdToken}`;

    const userInfo = await fetch(googleApiUrl, {
      method: 'GET',
    }).then((res) => res.json());

    if (!userInfo.email) {
      this.logger.log(`auth.service - getGoogleEmail : EXPIRED_TOKEN`);
      throw new CatchyException(
        'EXPIRED_TOKEN',
        HTTP_STATUS_CODE.WRONG_TOKEN,
        ERROR_CODE.EXPIRED_TOKEN,
      );
    }

    return userInfo.email;
  }

  async isExistEmail(email: string): Promise<boolean> {
    const user = await this.userRepository.findUserByEmail(email);

    if (!user) {
      return false;
    }

    return true;
  }
}
