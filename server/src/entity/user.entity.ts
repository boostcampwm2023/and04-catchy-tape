import {
  Column,
  Entity,
  CreateDateColumn,
  BaseEntity,
  PrimaryColumn,
} from 'typeorm';

@Entity({ name: 'user' })
export class User extends BaseEntity {
  @PrimaryColumn()
  userId: string;

  @Column()
  nickname: string;

  @Column({ nullable: true })
  photo: string | null;

  @Column()
  user_email: string;

  @CreateDateColumn()
  created_at: Date;
}
