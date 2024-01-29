import { Test, TestingModule } from '@nestjs/testing';
import { UserService } from './user.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { DataSource, Repository } from 'typeorm';
import { User } from '../entity/user.entity';
import { Recent_Played } from 'src/entity/recent_played.entity';
import { PassportModule } from '@nestjs/passport';
import { MusicRepository } from 'src/music/music.repository';

describe('UserService', () => {
  let service: UserService;
  let userRepository: Repository<User>;
  let mockDataSource: jest.Mocked<DataSource>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [PassportModule.register({ defaultStrategy: 'jwt' })],
      providers: [
        UserService,
        {
          provide: getRepositoryToken(User),
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

    service = module.get<UserService>(UserService);
    userRepository = module.get(getRepositoryToken(User));
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
