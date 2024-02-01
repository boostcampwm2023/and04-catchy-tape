import { Test, TestingModule } from '@nestjs/testing';
import { AuthService } from './auth.service';
import { Repository, DataSource } from 'typeorm';
import { getRepositoryToken } from '@nestjs/typeorm';
import { User } from 'src/entity/user.entity';
import { JwtModule } from '@nestjs/jwt';
import { PlaylistService } from 'src/playlist/playlist.service';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { PassportModule } from '@nestjs/passport';
import { PlaylistRepository } from 'src/playlist/playlist.repository';
import { MusicRepository } from 'src/music/music.repository';
import { CacheModule } from '@nestjs/cache-manager';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { UserRepository } from 'src/user/user.repository';

describe('AuthService', () => {
  let service: AuthService;
  let jwtModule: JwtModule;
  let userRepository: UserRepository;
  let playlistService: PlaylistService;
  let mockDataSource: jest.Mocked<DataSource>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [
        PassportModule.register({ defaultStrategy: 'jwt' }),
        JwtModule,
        CacheModule.register({}),
        ConfigModule,
      ],
      providers: [
        AuthService,
        ConfigService,
        {
          provide: UserRepository,
          useClass: Repository,
        },
        {
          provide: PlaylistRepository,
          useClass: Repository,
        },
        {
          provide: MusicRepository,
          useClass: Repository,
        },
        {
          provide: getRepositoryToken(Music_Playlist),
          useClass: Repository,
        },
        PlaylistService,
        {
          provide: DataSource,
          useValue: mockDataSource,
        },
      ],
    }).compile();

    service = module.get<AuthService>(AuthService);
    jwtModule = module.get<JwtModule>(JwtModule);
    userRepository = module.get<UserRepository>(UserRepository);
    playlistService = module.get<PlaylistService>(PlaylistService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
