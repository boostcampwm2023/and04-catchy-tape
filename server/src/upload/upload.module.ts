import { Module } from '@nestjs/common';
import { UploadController } from './upload.controller';
import { UploadService } from './upload.service';
import { NcloudConfigService } from 'src/config/ncloud.config';
import { AuthModule } from 'src/auth/auth.module';
import { Logger } from 'winston';
import { GreenEyeService } from 'src/config/greenEye.service';

@Module({
  imports: [AuthModule],
  controllers: [UploadController],
  providers: [UploadService, NcloudConfigService, Logger, GreenEyeService],
})
export class UploadModule {}
