import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import * as request from 'supertest';
import { MusicService } from './music.service';
import { UploadService } from 'src/upload/upload.service';
import { NcloudConfigService } from 'src/config/ncloud.config';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { getRepositoryToken } from '@nestjs/typeorm';
import { DataSource, Repository } from 'typeorm';
import { musicCreateInfo, user } from 'test/constants/music.mockData';
import { MusicController } from './music.controller';
import { User } from 'src/entity/user.entity';
import { AuthService } from 'src/auth/auth.service';
import { JwtService } from '@nestjs/jwt';
import { PassportModule } from '@nestjs/passport';
import { GreenEyeService } from 'src/config/greenEye.config';
import { JwtStrategy } from 'src/auth/jwt.strategy';
import { MusicRepository } from '../repository/music.repository';
import { CacheModule } from '@nestjs/cache-manager';
import { UserRepository } from 'src/repository/user.repository';

describe('UploadController', () => {
  let app: INestApplication;
  let musicController: MusicController;
  let musicService: MusicService;
  let uploadService: UploadService;
  let cloudService: NcloudConfigService;
  let configService: ConfigService;
  let authService: AuthService;
  let musicRepository: MusicRepository;
  let mockJwtStrategy = { validate: () => user };
  let mockDataSource: jest.Mocked<DataSource>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [PassportModule, CacheModule.register({}), ConfigModule],
      controllers: [MusicController],
      providers: [
        MusicService,
        UploadService,
        NcloudConfigService,
        ConfigService,
        GreenEyeService,
        AuthService,
        JwtService,
        JwtStrategy,
        {
          provide: UserRepository,
          useClass: Repository,
        },
        {
          provide: MusicRepository,
          useClass: Repository,
        },
        {
          provide: DataSource,
          useValue: mockDataSource,
        },
      ],
    })
      .overrideProvider(JwtStrategy)
      .useValue(mockJwtStrategy)
      .compile();

    configService = module.get<ConfigService>(ConfigService);
    cloudService = module.get<NcloudConfigService>(NcloudConfigService);
    uploadService = module.get<UploadService>(UploadService);
    authService = module.get<AuthService>(AuthService);
    musicController = module.get<MusicController>(MusicController);
    musicService = module.get<MusicService>(MusicService);
    musicRepository = module.get<MusicRepository>(MusicRepository);
    mockDataSource = {
      createQueryRunner: jest.fn(),
    } as unknown as jest.Mocked<DataSource>;

    app = module.createNestApplication();
    await app.init();
  });

  describe('MusicController 환경 확인', () => {
    it('MusicController should be defined', () => {
      expect(musicController).toBeDefined();
    });
  });

  describe('Music 요청 관련 테스트', () => {
    it('User가 요청 보낸 음악 정보를 DB에 저장한다.', async () => {
      request(app.getHttpServer)
        .post('/musics')
        .send({ musicCreateInfo })
        .expect(200)
        .expect(({ body }) => {
          expect(body.music_id).toEqual(musicCreateInfo.music_id);
        });
    });
  });
});
