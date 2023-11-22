import { Controller } from '@nestjs/common';
import { PlaylistService } from './playlist.service';

@Controller('playlist')
export class PlaylistController {
    constructor(private playlistService: PlaylistService) {}
}
