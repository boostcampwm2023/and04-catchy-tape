import { Injectable } from '@nestjs/common';
import { UserCreateDto } from 'src/dto/userNickname.dto';
import { User } from 'src/entity/user.entity';
import { Repository } from 'typeorm';
import { v4 as uuid } from 'uuid';

@Injectable()
export class UserService {
    //TODO: custom repository로 변경하기
    constructor(private userRepository: Repository<User>) {}

    createUser(userCreateDto: UserCreateDto) {
        const {nickname, email} = userCreateDto;
        const newUser: User = {
            userId: uuid(),
            nickname,
            photo: null,
            user_email: email,
            created_at: new Date()
        }
        const user = this.userRepository.create(newUser);
        this.userRepository.save(user);
    }
}
