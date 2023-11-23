import { Test, TestingModule } from '@nestjs/testing';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { User } from 'src/entity/user.entity';
import { Repository } from 'typeorm';
import { PlaylistService } from 'src/playlist/playlist.service';
import { Music } from 'src/entity/music.entity';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { Playlist } from 'src/entity/playlist.entity';

describe('UserController', () => {
  let controller: UserController;
  let userService: UserService;
  let playlistService: PlaylistService;
  let userRepository: Repository<User>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UserController],
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

    controller = module.get<UserController>(UserController);
    userService = module.get<UserService>(UserService);
    playlistService = module.get<PlaylistService>(PlaylistService);
    userRepository = module.get(getRepositoryToken(User));
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
