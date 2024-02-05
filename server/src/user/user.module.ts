import { Module } from '@nestjs/common';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from 'src/entity/user.entity';
import { AuthModule } from 'src/auth/auth.module';
import { Logger } from 'winston';
import { Recent_Played } from 'src/entity/recent_played.entity';
import { MusicRepository } from 'src/repository/music.repository';
import { UserRepository } from '../repository/user.repository';
import { Recent_PlayedRepository } from 'src/repository/recent_played.repository';

@Module({
  imports: [TypeOrmModule.forFeature([User, Recent_Played]), AuthModule],
  controllers: [UserController],
  providers: [
    UserService,
    Logger,
    MusicRepository,
    Recent_PlayedRepository,
    UserRepository,
  ],
})
export class UserModule {}
