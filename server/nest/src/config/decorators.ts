import { ExecutionContext, createParamDecorator } from '@nestjs/common';
import { User } from 'src/entity/user.entity';

export const ReqUser = createParamDecorator(
  (data: unknown, ctx: ExecutionContext): User => {
    const request = ctx.switchToHttp().getRequest();
    const user = request.user;

    return user;
  },
);
