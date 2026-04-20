import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn } from 'typeorm';

@Entity('admin_plans')
export class AdminPlan {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ type: 'text' })
  draftInsights: string;

  @Column({ type: 'float' })
  confidence: number;

  @Column({ default: false })
  requiresHumanApproval: boolean;

  @Column({ default: 'DRAFT' })
  status: 'DRAFT' | 'APPROVED' | 'REJECTED';

  @CreateDateColumn()
  createdAt: Date;

  @Column({ nullable: true })
  reviewerComment: string;
}
