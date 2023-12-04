import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { winstonLogger } from './config/logger.config';

async function bootstrap() {
  const app = await NestFactory.create(AppModule, { logger: winstonLogger });
  await app.listen(3000);
}
bootstrap();
