import { Module } from '@nestjs/common';
import { PlaylistService } from './playlist.service';
import { PlaylistController } from './playlist.controller';
import { Playlist } from 'src/entity/playlist.entity';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AuthModule } from 'src/auth/auth.module';
import { Music_Playlist } from 'src/entity/music_playlist.entity';
import { Music } from 'src/entity/music.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Playlist, Music_Playlist, Music]), AuthModule],
  controllers: [PlaylistController],
  providers: [PlaylistService],
})
export class PlaylistModule {}
