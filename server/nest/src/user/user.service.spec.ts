import { Test, TestingModule } from '@nestjs/testing';
import { UserService } from './user.service';
import { DataSource, Repository } from 'typeorm';
import { PassportModule } from '@nestjs/passport';
import { MusicRepository } from 'src/repository/music.repository';
import { UserRepository } from '../repository/user.repository';
import { Recent_PlayedRepository } from 'src/repository/recent_played.repository';

describe('UserService', () => {
  let service: UserService;
  let userRepository: UserRepository;
  let mockDataSource: jest.Mocked<DataSource>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [PassportModule.register({ defaultStrategy: 'jwt' })],
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

    service = module.get<UserService>(UserService);
    userRepository = module.get<UserRepository>(UserRepository);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
