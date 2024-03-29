import { HttpException } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/codes/httpStatusCode.enum';
import { ERROR_CODE } from '../codes/errorCode.enum';

export class CatchyException extends HttpException {
  constructor(
    message: string,
    statuscode: HTTP_STATUS_CODE,
    errorCode: ERROR_CODE,
  ) {
    super({ message, errorCode }, statuscode);
  }
}
