"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.StrategistService = void 0;
const common_1 = require("@nestjs/common");
const crypto_1 = require("crypto");
const llm_client_1 = require("../common/llm/llm.client");
const spring_fiagent_gateway_1 = require("./spring-fiagent.gateway");
let StrategistService = class StrategistService {
    springGateway;
    llmClient;
    draftStore = new Map();
    constructor(springGateway, llmClient) {
        this.springGateway = springGateway;
        this.llmClient = llmClient;
    }
    /**
     * Generates a draft business strategy plan.
     *
     * Human-in-the-loop rule:
     * - If confidence < 0.7, require explicit reviewer approval before usage.
     */
    async generateDraftInsights(input) {
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
        const draftId = (0, crypto_1.randomUUID)();
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
    async reviewDraft(input) {
        const draft = this.draftStore.get(input.draftId);
        if (!draft) {
            throw new common_1.NotFoundException('Draft insight not found.');
        }
        if (input.decision !== 'APPROVE' && input.decision !== 'REJECT') {
            throw new common_1.BadRequestException('Decision must be APPROVE or REJECT.');
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
};
exports.StrategistService = StrategistService;
exports.StrategistService = StrategistService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [spring_fiagent_gateway_1.SpringFiAgentGateway,
        llm_client_1.LlmClient])
], StrategistService);
//# sourceMappingURL=strategist.service.js.map