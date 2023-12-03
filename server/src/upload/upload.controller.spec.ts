import { Test, TestingModule } from '@nestjs/testing';
import { UploadController } from './upload.controller';
import { UploadService } from './upload.service';
import { NcloudConfigService } from 'src/config/ncloud.config';
import { ConfigService } from '@nestjs/config';
import * as fs from 'fs';
import { Readable } from 'stream';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';

describe('UploadController', () => {
  let app: INestApplication;
  let controller: UploadController;
  let service: UploadService;
  let cloudService: NcloudConfigService;
  let configService: ConfigService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [],
      controllers: [UploadController],
      providers: [UploadService, NcloudConfigService, ConfigService],
    }).compile();

    configService = module.get<ConfigService>(ConfigService);
    cloudService = module.get<NcloudConfigService>(NcloudConfigService);
    service = module.get<UploadService>(UploadService);
    controller = module.get<UploadController>(UploadController);

    app = module.createNestApplication();
    await app.init();
  });

  describe('Upload 컨트롤러 환경 확인', () => {
    it('Upload 컨트롤러 should be defined', () => {
      expect(controller).toBeDefined();
    });
  });
});
