import { Test, TestingModule } from '@nestjs/testing';
import { UserService } from './user.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from '../entity/user.entity';
import { Playlist } from 'src/entity/playlist.entity';
import { Music } from 'src/entity/music.entity';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { PlaylistService } from 'src/playlist/playlist.service';

describe('UserService', () => {
  let service: UserService;
  let playlistService: PlaylistService;
  let userRepository: Repository<User>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UserService,
        PlaylistService,
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

    service = module.get<UserService>(UserService);
    playlistService = module.get<PlaylistService>(PlaylistService);
    userRepository = module.get(getRepositoryToken(User));
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
