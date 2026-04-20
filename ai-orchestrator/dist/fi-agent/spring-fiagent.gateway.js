"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.SpringFiAgentGateway = void 0;
const common_1 = require("@nestjs/common");
/**
 * Gateway for FI-Agent Spring backend endpoints.
 */
let SpringFiAgentGateway = class SpringFiAgentGateway {
    springBaseUrl = process.env.FI_AGENT_SPRING_URL ?? 'http://localhost:8080/api/v1/fi-agent';
    async getTopProductsByStyleVector(styleVector, limit) {
        const response = await fetch(`${this.springBaseUrl}/stylist/top-products`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ styleVector, limit }),
        });
        if (!response.ok) {
            throw new common_1.InternalServerErrorException('Failed to retrieve top products from Spring backend.');
        }
        return (await response.json());
    }
    async calculateBatchScores(payload) {
        const response = await fetch(`${this.springBaseUrl}/scoring/batch`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        if (!response.ok) {
            throw new common_1.InternalServerErrorException('Failed to compute FI-Agent scores in Spring backend.');
        }
        return (await response.json());
    }
};
exports.SpringFiAgentGateway = SpringFiAgentGateway;
exports.SpringFiAgentGateway = SpringFiAgentGateway = __decorate([
    (0, common_1.Injectable)()
], SpringFiAgentGateway);
//# sourceMappingURL=spring-fiagent.gateway.js.map