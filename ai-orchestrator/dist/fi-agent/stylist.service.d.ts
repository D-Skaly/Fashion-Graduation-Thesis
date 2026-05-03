import { LlmProvider } from '../common/llm/llm-provider.interface';
import { StylistAdviceRequestDto, StylistAdviceResponseDto } from './dto/stylist.dto';
import { SpringFiAgentGateway } from './spring-fiagent.gateway';
export declare class StylistService {
    private readonly springGateway;
    private readonly llmProvider;
    constructor(springGateway: SpringFiAgentGateway, llmProvider: LlmProvider);
    /**
     * Workflow:
     * 1) Ask Spring backend for top-N style-nearest products.
     * 2) Ask LLM to transform candidates into actionable styling advice.
     */
    generateAdvice(input: StylistAdviceRequestDto): Promise<StylistAdviceResponseDto>;
}
