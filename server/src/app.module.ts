import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { UserModule } from './user/user.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { TypeOrmConfigService } from 'src/config/typeorm.config';
import { ConfigModule } from '@nestjs/config';
import { UploadModule } from './upload/upload.module';
import { MusicModule } from './music/music.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: __dirname + `/../${process.env.NODE_ENV}.env`,
    }),
    TypeOrmModule.forRootAsync({ useClass: TypeOrmConfigService }),
    UserModule,
    UploadModule,
    MusicModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
