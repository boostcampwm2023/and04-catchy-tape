import { Module } from '@nestjs/common';
import { PlaylistService } from './playlist.service';
import { PlaylistController } from './playlist.controller';
import { Playlist } from 'src/entity/playlist.entity';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AuthModule } from 'src/auth/auth.module';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { Music } from 'src/entity/music.entity';
import { PlaylistRepository } from '../repository/playlist.repository';
import { Logger } from 'winston';
import { MusicRepository } from 'src/repository/music.repository';
import { Music_PlaylistRepository } from 'src/repository/music_playlist.repository';

@Module({
  imports: [
    TypeOrmModule.forFeature([Playlist, Music_Playlist, Music]),
    AuthModule,
  ],
  controllers: [PlaylistController],
  providers: [
    PlaylistService,
    PlaylistRepository,
    Logger,
    MusicRepository,
    Music_PlaylistRepository,
  ],
  exports: [PlaylistService],
})
export class PlaylistModule {}
