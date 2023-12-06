import { Module } from '@nestjs/common';
import { PlaylistService } from './playlist.service';
import { PlaylistController } from './playlist.controller';
import { Playlist } from 'src/entity/playlist.entity';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AuthModule } from 'src/auth/auth.module';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { Music } from 'src/entity/music.entity';
import { Logger } from 'winston';
import { Recent_Played } from 'src/entity/recent_played.entity';

@Module({
  imports: [
    TypeOrmModule.forFeature([Playlist, Music_Playlist, Music, Recent_Played]),
    AuthModule,
  ],
  controllers: [PlaylistController],
  providers: [PlaylistService, Logger],
  exports: [PlaylistService],
})
export class PlaylistModule {}
