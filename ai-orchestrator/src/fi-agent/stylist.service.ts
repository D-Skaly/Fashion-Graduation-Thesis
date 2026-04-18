import { Injectable } from '@nestjs/common';
import { LlmClient } from '../common/llm/llm.client';
import { StylistAdviceRequestDto, StylistAdviceResponseDto } from './dto/stylist.dto';
import { SpringFiAgentGateway } from './spring-fiagent.gateway';

@Injectable()
export class StylistService {
  constructor(
    private readonly springGateway: SpringFiAgentGateway,
    private readonly llmClient: LlmClient,
  ) {}

  /**
   * Workflow:
   * 1) Ask Spring backend for top-N style-nearest products.
   * 2) Ask LLM to transform candidates into actionable styling advice.
   */
  async generateAdvice(input: StylistAdviceRequestDto): Promise<StylistAdviceResponseDto> {
    const candidates = await this.springGateway.getTopProductsByStyleVector(
      input.styleVector,
      input.limit,
    );

    const prompt = `
You are FI-Agent Stylist, a premium fashion assistant.
Given the candidate products, generate concise, practical outfit guidance.

Customer context:
${input.customerContext ?? 'No additional context provided'}

Candidate products:
${JSON.stringify(candidates, null, 2)}

Rules:
- Suggest combinations (top/bottom/layer/accessory logic when possible).
- Mention why each recommendation fits the user profile.
- Keep tone consultative and specific.
`;

    const advice = await this.llmClient.complete(prompt);

    return {
      recommendedProducts: candidates,
      advice,
    };
  }
}
