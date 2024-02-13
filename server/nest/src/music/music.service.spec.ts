import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import { MusicService } from './music.service';
import { UploadService } from 'src/upload/upload.service';
import { NcloudConfigService } from 'src/config/ncloud.config';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { DataSource, Repository } from 'typeorm';
import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { CatchyException } from 'src/config/catchyException';
import { faultGenreMusicCreateInfo } from 'test/constants/music.mockData';
import { GreenEyeService } from 'src/config/greenEye.config';
import { MusicRepository } from '../repository/music.repository';
import { CacheModule } from '@nestjs/cache-manager';
import { UserRepository } from 'src/repository/user.repository';
import { Recent_PlayedRepository } from 'src/repository/recent_played.repository';

describe('UploadController', () => {
  let app: INestApplication;
  let musicService: MusicService;
  let uploadService: UploadService;
  let cloudService: NcloudConfigService;
  let configService: ConfigService;
  let mockRepository: jest.Mocked<MusicRepository>;
  let mockDataSource: jest.Mocked<DataSource>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [ConfigModule, CacheModule.register({})],
      controllers: [],
      providers: [
        MusicService,
        UploadService,
        NcloudConfigService,
        ConfigService,
        GreenEyeService,
        {
          provide: UserRepository,
          useClass: Repository,
        },
        {
          provide: MusicRepository,
          useClass: Repository,
        },
        {
          provide: Recent_PlayedRepository,
          useClass: Repository,
        },
        {
          provide: DataSource,
          useValue: mockDataSource,
        },
      ],
    }).compile();

    configService = module.get<ConfigService>(ConfigService);
    cloudService = module.get<NcloudConfigService>(NcloudConfigService);
    uploadService = module.get<UploadService>(UploadService);
    mockDataSource = {
      createQueryRunner: jest.fn().mockResolvedValue({
        startTransaction: jest.fn(),
        commitTransaction: jest.fn(),
        rollbackTransaction: jest.fn(),
        release: jest.fn(),
      }),
    } as unknown as jest.Mocked<DataSource>;
    mockRepository = {
      create: jest.fn(),
      save: jest.fn(),
    } as unknown as jest.Mocked<MusicRepository>;
    musicService = new MusicService(
      mockRepository,
      uploadService,
      cloudService,
      mockDataSource,
    );
  });

  describe('MusicService 환경 확인', () => {
    it('MusicService should be defined', () => {
      expect(musicService).toBeDefined();
    });
  });

  it('잘못된 장르로 요청을 보내면 CatchyException이 발생한다.', async () => {
    const musicInfo: MusicCreateDto = faultGenreMusicCreateInfo;

    const userId: string = 'user_id';

    await expect(async () => {
      await musicService.createMusic(musicInfo, userId);
    }).rejects.toThrow(CatchyException);
  });
});
