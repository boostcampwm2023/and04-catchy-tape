import { Module } from '@nestjs/common';
import { UploadController } from './upload.controller';
import { UploadService } from './upload.service';
import { NcloudConfigService } from 'src/config/ncloud.config';
import { HttpModule } from '@nestjs/axios';
import { AuthModule } from 'src/auth/auth.module';

@Module({
  imports: [HttpModule, AuthModule],
  controllers: [UploadController],
  providers: [UploadService, NcloudConfigService],
})
export class UploadModule {}
