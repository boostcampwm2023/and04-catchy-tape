import { Test, TestingModule } from '@nestjs/testing';
import { AuthService } from './auth.service';
import { Repository, DataSource } from 'typeorm';
import { getRepositoryToken } from '@nestjs/typeorm';
import { User } from 'src/entity/user.entity';
import { JwtModule, JwtService } from '@nestjs/jwt';
import { PlaylistService } from 'src/playlist/playlist.service';
import { Playlist } from 'src/entity/playlist.entity';
import { Music } from 'src/entity/music.entity';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { PassportModule } from '@nestjs/passport';
import { PlaylistRepository } from 'src/playlist/playlist.repository';
import { MusicRepository } from 'src/music/music.repository';

describe('AuthService', () => {
  let service: AuthService;
  let jwtModule: JwtModule;
  let userRepository: Repository<User>;
  let playlistService: PlaylistService;
  let mockDataSource: jest.Mocked<DataSource>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [PassportModule.register({ defaultStrategy: 'jwt' }), JwtModule],
      providers: [
        AuthService,
        {
          provide: getRepositoryToken(User),
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
    userRepository = module.get(getRepositoryToken(User));
    playlistService = module.get<PlaylistService>(PlaylistService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
