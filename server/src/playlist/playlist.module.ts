import { Module } from '@nestjs/common';
import { PlaylistService } from './playlist.service';
import { PlaylistController } from './playlist.controller';

@Module({
  controllers: [PlaylistController],
  providers: [PlaylistService]
})
export class PlaylistModule {}
