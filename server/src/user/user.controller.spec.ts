import { Test, TestingModule } from '@nestjs/testing';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { User } from 'src/entity/user.entity';
import { DataSource, Repository } from 'typeorm';
import { Recent_Played } from 'src/entity/recent_played.entity';
import { MusicRepository } from 'src/music/music.repository';
import { UserRepository } from './user.repository';

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
          provide: getRepositoryToken(Recent_Played),
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
