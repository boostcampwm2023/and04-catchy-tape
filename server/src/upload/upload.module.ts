import { Module } from '@nestjs/common';
import { UploadController } from './upload.controller';
import { UploadService } from './upload.service';
import { NcloudConfigService } from 'src/config/ncloud.config';
import { AuthModule } from 'src/auth/auth.module';
import { GreenEyeService } from '../config/greenEye.service';

@Module({
  imports: [AuthModule],
  controllers: [UploadController],
  providers: [UploadService, NcloudConfigService, GreenEyeService],
})
export class UploadModule {}
