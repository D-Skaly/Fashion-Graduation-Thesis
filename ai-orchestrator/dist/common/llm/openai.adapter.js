"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var OpenAiAdapter_1;
Object.defineProperty(exports, "__esModule", { value: true });
exports.OpenAiAdapter = void 0;
const common_1 = require("@nestjs/common");
/**
 * Adapter for OpenAI LLM provider.
 */
let OpenAiAdapter = OpenAiAdapter_1 = class OpenAiAdapter {
    logger = new common_1.Logger(OpenAiAdapter_1.name);
    async complete(prompt) {
        const apiKey = process.env.OPENAI_API_KEY;
        if (!apiKey) {
            throw new common_1.InternalServerErrorException('OPENAI_API_KEY is missing for orchestrator LLM calls.');
        }
        const model = process.env.OPENAI_MODEL ?? 'gpt-4.1-mini';
        const url = process.env.OPENAI_BASE_URL ?? 'https://api.openai.com/v1/responses';
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${apiKey}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                model,
                input: prompt,
            }),
        });
        if (!response.ok) {
            this.logger.error(`LLM call failed with status ${response.status}`);
            throw new common_1.InternalServerErrorException('LLM provider call failed.');
        }
        const payload = (await response.json());
        // Responses API returns output_text on newer versions; fallback included for compatibility.
        const text = payload.output_text ??
            payload.output?.flatMap((item) => item.content ?? []).map((c) => c.text ?? '').join('\n');
        if (!text || !text.trim()) {
            throw new common_1.InternalServerErrorException('LLM provider returned empty output.');
        }
        return text.trim();
    }
};
exports.OpenAiAdapter = OpenAiAdapter;
exports.OpenAiAdapter = OpenAiAdapter = OpenAiAdapter_1 = __decorate([
    (0, common_1.Injectable)()
], OpenAiAdapter);
//# sourceMappingURL=openai.adapter.js.map