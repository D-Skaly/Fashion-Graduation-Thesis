import { Repository } from 'typeorm';
import { LlmProvider } from '../common/llm/llm-provider.interface';
import { StrategistDraftInsightDto, StrategistFinalInsightDto, StrategistInsightRequestDto, StrategistReviewDto } from './dto/strategist.dto';
import { SpringFiAgentGateway } from './spring-fiagent.gateway';
import { AdminPlan } from './entities/admin-plan.entity';
export declare class StrategistService {
    private readonly springGateway;
    private readonly llmProvider;
    private readonly adminPlanRepository;
    constructor(springGateway: SpringFiAgentGateway, llmProvider: LlmProvider, adminPlanRepository: Repository<AdminPlan>);
    generateDraftInsights(input: StrategistInsightRequestDto): Promise<StrategistDraftInsightDto>;
    createPlanFromGoal(goal: string): Promise<AdminPlan>;
    reviewDraft(input: StrategistReviewDto): Promise<StrategistFinalInsightDto>;
}
