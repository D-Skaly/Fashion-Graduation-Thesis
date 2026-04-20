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
exports.StylistService = void 0;
const common_1 = require("@nestjs/common");
const llm_client_1 = require("../common/llm/llm.client");
const spring_fiagent_gateway_1 = require("./spring-fiagent.gateway");
let StylistService = class StylistService {
    springGateway;
    llmClient;
    constructor(springGateway, llmClient) {
        this.springGateway = springGateway;
        this.llmClient = llmClient;
    }
    /**
     * Workflow:
     * 1) Ask Spring backend for top-N style-nearest products.
     * 2) Ask LLM to transform candidates into actionable styling advice.
     */
    async generateAdvice(input) {
        const candidates = await this.springGateway.getTopProductsByStyleVector(input.styleVector, input.limit);
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
};
exports.StylistService = StylistService;
exports.StylistService = StylistService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [spring_fiagent_gateway_1.SpringFiAgentGateway,
        llm_client_1.LlmClient])
], StylistService);
//# sourceMappingURL=stylist.service.js.map