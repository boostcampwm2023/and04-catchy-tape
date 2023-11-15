import {
  Column,
  Entity,
  CreateDateColumn,
  BaseEntity,
  OneToOne,
  JoinColumn,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { User } from './user.entity';

@Entity({ name: 'music' })
export class Music extends BaseEntity {
  @PrimaryGeneratedColumn()
  musicId: string;

  @Column()
  title: string;

  @Column({ nullable: true })
  lyrics: string | null;

  @Column()
  cover: string;

  @Column()
  musicFile: string;

  @CreateDateColumn()
  created_at: Date;

  @OneToOne(() => User)
  @JoinColumn({ name: 'user_id' })
  userId: string;
}
