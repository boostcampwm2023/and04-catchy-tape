import { Module } from '@nestjs/common';
import { UploadController } from './upload.controller';
import { UploadService } from './upload.service';
import { NcloudConfigService } from 'src/config/ncloud.config';

@Module({
  controllers: [UploadController],
  providers: [UploadService, NcloudConfigService],
})
export class UploadModule {}
