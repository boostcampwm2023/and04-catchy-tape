import { Column, Entity, Unique, CreateDateColumn } from "typeorm";

@Entity({name: 'user'})
@Unique(['userId'])
export class User {
    @Column()
    userId: string;

    @Column()   
    nickname: string;

    @Column({nullable: true})
    photo: string | null;

    @Column()
    user_email: string;

    @CreateDateColumn()
    created_at: Date;
}