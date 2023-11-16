import { IsNotEmpty, IsString, Matches } from "class-validator";

export class UserCreateDto {
    @IsNotEmpty()
    @IsString()
    nickname: string;

    @IsNotEmpty()
    @IsString()
    idToken: string;
}