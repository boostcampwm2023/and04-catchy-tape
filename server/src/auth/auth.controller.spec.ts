import { Test, TestingModule } from '@nestjs/testing';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { User } from 'src/entity/user.entity';
import { Repository } from 'typeorm';
import { JwtModule, JwtService } from '@nestjs/jwt';
import { Playlist } from 'src/entity/playlist.entity';
import { Music } from 'src/entity/music.entity';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { Logger } from '@nestjs/common';
import { PassportModule } from '@nestjs/passport';

describe('AuthController', () => {
  let controller: AuthController;
  let service: AuthService;
  let jwtModule: JwtModule;
  let userRepository: Repository<User>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [PassportModule.register({ defaultStrategy: 'jwt' }), JwtModule],
      controllers: [AuthController],
      providers: [
        Logger,
        AuthService,
        {
          provide: getRepositoryToken(User),
          useClass: Repository,
        },
        {
          provide: getRepositoryToken(Playlist),
          useClass: Repository,
        },
        {
          provide: getRepositoryToken(Music),
          useClass: Repository,
        },
        {
          provide: getRepositoryToken(Music_Playlist),
          useClass: Repository,
        },
      ],
    }).compile();

    controller = module.get<AuthController>(AuthController);
    service = module.get<AuthService>(AuthService);
    jwtModule = module.get<JwtModule>(JwtModule);
    userRepository = module.get(getRepositoryToken(User));
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
