import { BadRequestException, Injectable, NotFoundException } from '@nestjs/common';
import { randomUUID } from 'crypto';
import { LlmClient } from '../common/llm/llm.client';
import {
  StrategistDraftInsightDto,
  StrategistFinalInsightDto,
  StrategistInsightRequestDto,
  StrategistReviewDto,
} from './dto/strategist.dto';
import { SpringFiAgentGateway } from './spring-fiagent.gateway';

interface DraftStoreItem {
  draftedAt: string;
  requiresHumanApproval: boolean;
  confidence: number;
  draftInsights: string;
}

@Injectable()
export class StrategistService {
  private readonly draftStore = new Map<string, DraftStoreItem>();

  constructor(
    private readonly springGateway: SpringFiAgentGateway,
    private readonly llmClient: LlmClient,
  ) {}

  /**
   * Generates a draft business strategy plan.
   *
   * Human-in-the-loop rule:
   * - If confidence < 0.7, require explicit reviewer approval before usage.
   */
  async generateDraftInsights(
    input: StrategistInsightRequestDto,
  ): Promise<StrategistDraftInsightDto> {
    const scores = await this.springGateway.calculateBatchScores({
      weights: {
        wFit: input.wFit,
        wPush: input.wPush,
        wTrend: input.wTrend,
      },
      products: input.products.map((p) => ({
        productId: p.productId,
        styleScore: p.styleScore,
        financeMultiplier: p.financeMultiplier,
        businessScore: p.businessScore,
        marketScore: p.marketScore,
      })),
    });

    const topScoredProducts = scores.slice(0, input.topN);
    const aggregateGap = input.products.reduce((acc, p) => acc + p.gapAnalysisScore, 0) / input.products.length;
    const aggregateCr = input.products.reduce((acc, p) => acc + p.conversionRate, 0) / input.products.length;

    // Heuristic confidence estimator: combine signal quality and conversion stability.
    const confidence = Math.max(0, Math.min(1, (aggregateCr * 0.6) + ((1 - aggregateGap) * 0.4)));
    const requiresHumanApproval = confidence < 0.7;

    const prompt = `
You are FI-Agent Strategist.
Using scored products and metrics, provide specific recommendations for:
1) Pricing adjustments
2) Inventory reallocation
3) Campaign priorities

Mean conversion rate: ${aggregateCr.toFixed(4)}
Mean gap score: ${aggregateGap.toFixed(4)}
Top scored products:
${JSON.stringify(topScoredProducts, null, 2)}

If confidence is lower, explicitly label recommendations as "experimental".
`;

    const draftInsights = await this.llmClient.complete(prompt);

    const draftId = randomUUID();
    this.draftStore.set(draftId, {
      draftedAt: new Date().toISOString(),
      requiresHumanApproval,
      confidence,
      draftInsights,
    });

    return {
      draftId,
      requiresHumanApproval,
      confidence,
      scoredProducts: topScoredProducts,
      draftInsights,
    };
  }

  async reviewDraft(input: StrategistReviewDto): Promise<StrategistFinalInsightDto> {
    const draft = this.draftStore.get(input.draftId);
    if (!draft) {
      throw new NotFoundException('Draft insight not found.');
    }

    if (input.decision !== 'APPROVE' && input.decision !== 'REJECT') {
      throw new BadRequestException('Decision must be APPROVE or REJECT.');
    }

    if (input.decision === 'REJECT') {
      return {
        approved: false,
        finalInsights: `Rejected by reviewer. Note: ${input.reviewerComment ?? 'No comment provided.'}`,
      };
    }

    const finalInsights = input.reviewerComment
      ? `${draft.draftInsights}\n\nReviewer note: ${input.reviewerComment}`
      : draft.draftInsights;

    return {
      approved: true,
      finalInsights,
    };
  }
}
