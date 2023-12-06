import { Module } from '@nestjs/common';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from 'src/entity/user.entity';
import { AuthModule } from 'src/auth/auth.module';
import { PlaylistService } from 'src/playlist/playlist.service';
import { Playlist } from 'src/entity/playlist.entity';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { Music } from 'src/entity/music.entity';
import { Logger } from 'winston';

@Module({
  imports: [
    TypeOrmModule.forFeature([User, Playlist, Music_Playlist, Music]),
    AuthModule,
  ],
  controllers: [UserController],
  providers: [UserService, PlaylistService, Logger],
})
export class UserModule {}
