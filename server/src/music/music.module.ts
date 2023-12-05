import { Module } from '@nestjs/common';
import { MusicController } from './music.controller';
import { MusicService } from './music.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Music } from 'src/entity/music.entity';
import { AuthModule } from 'src/auth/auth.module';
import { UploadService } from 'src/upload/upload.service';
import { NcloudConfigService } from 'src/config/ncloud.config';
import { GreenEyeService } from 'src/config/greenEye.service';

@Module({
  imports: [TypeOrmModule.forFeature([Music]), AuthModule],
  controllers: [MusicController],
  providers: [
    MusicService,
    UploadService,
    NcloudConfigService,
    GreenEyeService,
  ],
})
export class MusicModule {}
