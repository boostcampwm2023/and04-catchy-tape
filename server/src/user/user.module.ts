import { Module } from '@nestjs/common';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from 'src/entity/user.entity';
import { AuthModule } from 'src/auth/auth.module';
import { Logger } from 'winston';
import { Recent_Played } from 'src/entity/recent_played.entity';
import { MusicRepository } from 'src/music/music.repository';

@Module({
  imports: [TypeOrmModule.forFeature([User, Recent_Played]), AuthModule],
  controllers: [UserController],
  providers: [UserService, Logger, MusicRepository],
})
export class UserModule {}
