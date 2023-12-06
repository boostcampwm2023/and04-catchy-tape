import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import { ExecutionContext } from '@nestjs/common/interfaces';
import { MusicService } from './music.service';
import { UploadService } from 'src/upload/upload.service';
import { NcloudConfigService } from 'src/config/ncloud.config';
import { ConfigService } from '@nestjs/config';
import { getRepositoryToken } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Music } from 'src/entity/music.entity';
import { realMusicCreateInfo, user } from 'test/constants/music.mockData';
import { MusicController } from './music.controller';
import * as request from 'supertest';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { User } from 'src/entity/user.entity';
import { AuthService } from 'src/auth/auth.service';
import { JwtService } from '@nestjs/jwt';
import { PlaylistService } from 'src/playlist/playlist.service';
import { Playlist } from 'src/entity/playlist.entity';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { AuthGuard } from '@nestjs/passport';
import { GreenEyeService } from 'src/config/greenEye.service';

describe('UploadController', () => {
  let app: INestApplication;
  let musicController: MusicController;
  let uploadService: UploadService;
  let cloudService: NcloudConfigService;
  let configService: ConfigService;
  let authService: AuthService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [],
      controllers: [MusicController],
      providers: [
        MusicService,
        UploadService,
        NcloudConfigService,
        ConfigService,
        GreenEyeService,
        AuthService,
        PlaylistService,
        JwtService,
        {
          provide: getRepositoryToken(Music),
          useClass: Repository,
        },
        {
          provide: getRepositoryToken(User),
          useClass: Repository,
        },
        {
          provide: getRepositoryToken(Playlist),
          useClass: Repository,
        },
        {
          provide: getRepositoryToken(Music_Playlist),
          useClass: Repository,
        },
      ],
    })
      .overrideGuard(AuthGuard)
      .useValue({
        canActivate: (context: ExecutionContext) => {
          const req = context.switchToHttp().getRequest();
          req.user = user;
          return true;
        },
      })
      .compile();

    configService = module.get<ConfigService>(ConfigService);
    cloudService = module.get<NcloudConfigService>(NcloudConfigService);
    uploadService = module.get<UploadService>(UploadService);
    authService = module.get<AuthService>(AuthService);
    musicController = module.get<MusicController>(MusicController);

    app = module.createNestApplication();
    await app.init();
  });

  describe('MusicController 환경 확인', () => {
    it('MusicController should be defined', () => {
      expect(musicController).toBeDefined();
    });
  });
});
