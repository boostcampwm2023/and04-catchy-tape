import { HttpException, Injectable } from '@nestjs/common';
import { HTTP_STATUS_CODE } from 'src/httpStatusCode.enum';
import { UserCreateDto } from 'src/dto/userCreate.dto';
import { User } from 'src/entity/user.entity';
import { Repository } from 'typeorm';
import { v4 as uuid } from 'uuid';
import { InjectRepository } from '@nestjs/typeorm';

@Injectable()
export class UserService {
  //TODO: custom repository로 변경하기
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
  ) {}

  createUser(userCreateDto: UserCreateDto): string {
    const { nickname, email } = userCreateDto;
    const newUser: User = this.userRepository.create({
      userId: uuid(),
      nickname,
      photo: null,
      user_email: email,
      created_at: new Date(),
    });

    this.userRepository.save(newUser);
    return newUser.userId;
  }

  async isDuplicatedUserEmail(userNickname: string): Promise<boolean> {
    try {
      const user = await this.userRepository.findOneBy({
        nickname: userNickname,
      });

      if (user) {
        return true;
      }

      return false;
    } catch {
      throw new HttpException('SERVER ERROR', HTTP_STATUS_CODE.SERVER_ERROR);
    }
  }
}
