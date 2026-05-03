"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var TryOnConsumer_1;
Object.defineProperty(exports, "__esModule", { value: true });
exports.TryOnConsumer = void 0;
const bullmq_1 = require("@nestjs/bullmq");
const common_1 = require("@nestjs/common");
let TryOnConsumer = TryOnConsumer_1 = class TryOnConsumer extends bullmq_1.WorkerHost {
    logger = new common_1.Logger(TryOnConsumer_1.name);
    springCallbackUrl = process.env.FI_AGENT_SPRING_URL_CALLBACK ?? 'http://localhost:8080/api/v1/tryon/callback';
    aiServiceUrl = process.env.AI_SERVICE_URL ?? 'http://localhost:8001/tryon/process';
    async process(job) {
        const { userId, productId, userImageUrl, backendJobId } = job.data;
        this.logger.log(`Processing Try-on job ${job.id} (Backend: ${backendJobId}) for user ${userId}`);
        // Call Python AI Service (FastAPI) for heavy lifting
        let resultImageUrl = 'https://cdn.example.com/results/tryon_result.jpg';
        try {
            const aiResponse = await fetch(this.aiServiceUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ userId, productId, userImageUrl })
            });
            if (aiResponse.ok) {
                const aiResult = await aiResponse.json();
                resultImageUrl = aiResult.result_url || resultImageUrl;
                this.logger.log(`AI Service completed for job ${job.id}`);
            }
            else {
                this.logger.warn(`AI Service returned status ${aiResponse.status}, using default/cached result`);
            }
        }
        catch (error) {
            this.logger.error(`Failed to call AI Service: ${error.message}. Falling back to simulation.`);
            // Simulation fallback
            await new Promise(resolve => setTimeout(resolve, 2000));
        }
        if (backendJobId) {
            try {
                await fetch(this.springCallbackUrl, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        jobId: backendJobId,
                        status: 'COMPLETED',
                        resultImageUrl: resultImageUrl
                    })
                });
                this.logger.log(`Callback sent for job ${backendJobId}`);
            }
            catch (error) {
                this.logger.error(`Failed to send callback for job ${backendJobId}: ${error.message}`);
            }
        }
        this.logger.log(`Try-on job ${job.id} completed`);
        return { imageUrl: resultImageUrl };
    }
};
exports.TryOnConsumer = TryOnConsumer;
exports.TryOnConsumer = TryOnConsumer = TryOnConsumer_1 = __decorate([
    (0, bullmq_1.Processor)('virtual-try-on')
], TryOnConsumer);
//# sourceMappingURL=tryon.consumer.js.map