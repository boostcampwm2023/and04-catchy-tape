import { Body, Post, UsePipes, ValidationPipe } from "@nestjs/common";
import { UserService } from "./user.service";
import { UserCreateDto } from "src/dto/userNickname.dto";

export class UserController {
    constructor(private userService: UserService){}

    @Post('signup')
    @UsePipes(ValidationPipe)
    signup(@Body() userCreateDto: UserCreateDto) {
        this.userService.createUser(userCreateDto);
    }
}