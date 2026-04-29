import { BadRequestException, Inject, Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { LLM_PROVIDER, LlmProvider } from '../common/llm/llm-provider.interface';
import {
  StrategistDraftInsightDto,
  StrategistFinalInsightDto,
  StrategistInsightRequestDto,
  StrategistReviewDto,
} from './dto/strategist.dto';
import { SpringFiAgentGateway } from './spring-fiagent.gateway';
import { AdminPlan } from './entities/admin-plan.entity';

@Injectable()
export class StrategistService {
  constructor(
    private readonly springGateway: SpringFiAgentGateway,
    @Inject(LLM_PROVIDER)
    private readonly llmProvider: LlmProvider,
    @InjectRepository(AdminPlan)
    private readonly adminPlanRepository: Repository<AdminPlan>,
  ) {}

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

    const draftInsights = await this.llmProvider.complete(prompt);

    const draft = this.adminPlanRepository.create({
      draftInsights,
      confidence,
      requiresHumanApproval,
      status: 'DRAFT',
    });
    const savedDraft = await this.adminPlanRepository.save(draft);

    return {
      draftId: savedDraft.id,
      requiresHumanApproval,
      confidence,
      scoredProducts: topScoredProducts,
      draftInsights,
    };
  }

  async createPlanFromGoal(goal: string): Promise<AdminPlan> {
    const prompt = `
You are FI-Agent Strategist.
An admin has provided a goal for the business.
Goal: "${goal}"

Please provide a detailed strategic plan including:
1. Target products (categories/styles)
2. Pricing strategy (discounts/markups)
3. Inventory actions
4. Marketing emphasis

Response should be structured and professional.
`;

    const draftInsights = await this.llmProvider.complete(prompt);
    
    const confidence = 0.85;
    const requiresHumanApproval = true;

    const plan = this.adminPlanRepository.create({
      draftInsights,
      confidence,
      requiresHumanApproval,
      status: 'DRAFT',
    });
    
    return await this.adminPlanRepository.save(plan);
  }

  async reviewDraft(input: StrategistReviewDto): Promise<StrategistFinalInsightDto> {
    const draft = await this.adminPlanRepository.findOneBy({ id: input.draftId });
    if (!draft) {
      throw new NotFoundException('Draft insight not found.');
    }

    if (input.decision !== 'APPROVE' && input.decision !== 'REJECT') {
      throw new BadRequestException('Decision must be APPROVE or REJECT.');
    }

    if (input.decision === 'REJECT') {
      draft.status = 'REJECTED';
      draft.reviewerComment = input.reviewerComment ?? null;
      await this.adminPlanRepository.save(draft);

      return {
        approved: false,
        finalInsights: `Rejected by reviewer. Note: ${input.reviewerComment ?? 'No comment provided.'}`,
      };
    }

    draft.status = 'APPROVED';
    draft.reviewerComment = input.reviewerComment ?? null;
    await this.adminPlanRepository.save(draft);

    const finalInsights = input.reviewerComment
      ? `${draft.draftInsights}\n\nReviewer note: ${input.reviewerComment}`
      : draft.draftInsights;

    return {
      approved: true,
      finalInsights,
    };
  }
}