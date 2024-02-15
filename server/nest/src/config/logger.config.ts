import { WinstonModule, utilities } from 'nest-winston';
import * as winston from 'winston';
import * as winstonDaily from 'winston-daily-rotate-file';

const colors = {
  error: 'red',
  warn: 'yellow',
  info: 'green',
  http: 'magenta',
  silly: 'blue',
};
winston.addColors(colors);

const dailyOption = () => {
  return {
    datePattern: 'YYYY-MM-DD',
    dirname: `logs/`,
    filename: `%DATE%.log`,
    maxFiles: 7,
    zippedArchive: true,
    format: winston.format.combine(
      winston.format.timestamp(),
      utilities.format.nestLike(process.env.NODE_ENV, {
        colors: false,
        prettyPrint: true,
      }),
    ),
  };
};

export const winstonLogger = WinstonModule.createLogger({
  transports: [
    new winston.transports.Console({
      level: process.env.NODE_ENV === 'prod' ? 'info' : 'silly',
      format: winston.format.combine(
        winston.format.colorize({ all: true }),
        winston.format.timestamp(),
        utilities.format.nestLike('CatchyTape', { prettyPrint: true }),
      ),
    }),
    new winstonDaily(dailyOption()),
  ],
});
