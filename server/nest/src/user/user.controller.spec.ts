import { Test, TestingModule } from '@nestjs/testing';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { DataSource, Repository } from 'typeorm';
import { MusicRepository } from 'src/repository/music.repository';
import { UserRepository } from '../repository/user.repository';
import { Recent_PlayedRepository } from 'src/repository/recent_played.repository';

describe('UserController', () => {
  let controller: UserController;
  let userService: UserService;
  let userRepository: UserRepository;
  let mockDataSource: jest.Mocked<DataSource>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UserController],
      providers: [
        UserService,
        {
          provide: UserRepository,
          useClass: Repository,
        },
        {
          provide: Recent_PlayedRepository,
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
    }).compile();

    controller = module.get<UserController>(UserController);
    userService = module.get<UserService>(UserService);
    userRepository = module.get<UserRepository>(UserRepository);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
