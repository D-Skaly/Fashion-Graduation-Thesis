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
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.StrategistService = void 0;
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const typeorm_2 = require("typeorm");
const llm_provider_interface_1 = require("../common/llm/llm-provider.interface");
const spring_fiagent_gateway_1 = require("./spring-fiagent.gateway");
const admin_plan_entity_1 = require("./entities/admin-plan.entity");
let StrategistService = class StrategistService {
    springGateway;
    llmProvider;
    adminPlanRepository;
    constructor(springGateway, llmProvider, adminPlanRepository) {
        this.springGateway = springGateway;
        this.llmProvider = llmProvider;
        this.adminPlanRepository = adminPlanRepository;
    }
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
    async createPlanFromGoal(goal) {
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
    async reviewDraft(input) {
        const draft = await this.adminPlanRepository.findOneBy({ id: input.draftId });
        if (!draft) {
            throw new common_1.NotFoundException('Draft insight not found.');
        }
        if (input.decision !== 'APPROVE' && input.decision !== 'REJECT') {
            throw new common_1.BadRequestException('Decision must be APPROVE or REJECT.');
        }
        if (input.decision === 'REJECT') {
            draft.status = 'REJECTED';
            draft.reviewerComment = input.reviewerComment;
            await this.adminPlanRepository.save(draft);
            return {
                approved: false,
                finalInsights: `Rejected by reviewer. Note: ${input.reviewerComment ?? 'No comment provided.'}`,
            };
        }
        draft.status = 'APPROVED';
        draft.reviewerComment = input.reviewerComment;
        await this.adminPlanRepository.save(draft);
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
    __param(1, (0, common_1.Inject)(llm_provider_interface_1.LLM_PROVIDER)),
    __param(2, (0, typeorm_1.InjectRepository)(admin_plan_entity_1.AdminPlan)),
    __metadata("design:paramtypes", [spring_fiagent_gateway_1.SpringFiAgentGateway, Object, typeorm_2.Repository])
], StrategistService);
//# sourceMappingURL=strategist.service.js.map