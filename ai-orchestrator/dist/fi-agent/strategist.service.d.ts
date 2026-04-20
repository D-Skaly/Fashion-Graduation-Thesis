import { LlmClient } from '../common/llm/llm.client';
import { StrategistDraftInsightDto, StrategistFinalInsightDto, StrategistInsightRequestDto, StrategistReviewDto } from './dto/strategist.dto';
import { SpringFiAgentGateway } from './spring-fiagent.gateway';
export declare class StrategistService {
    private readonly springGateway;
    private readonly llmClient;
    private readonly draftStore;
    constructor(springGateway: SpringFiAgentGateway, llmClient: LlmClient);
    /**
     * Generates a draft business strategy plan.
     *
     * Human-in-the-loop rule:
     * - If confidence < 0.7, require explicit reviewer approval before usage.
     */
    generateDraftInsights(input: StrategistInsightRequestDto): Promise<StrategistDraftInsightDto>;
    reviewDraft(input: StrategistReviewDto): Promise<StrategistFinalInsightDto>;
}
