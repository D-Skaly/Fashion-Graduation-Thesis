import { Processor, WorkerHost } from '@nestjs/bullmq';
import { Job } from 'bullmq';
import { Logger } from '@nestjs/common';

@Processor('virtual-try-on')
export class TryOnConsumer extends WorkerHost {
  private readonly logger = new Logger(TryOnConsumer.name);
  private readonly springCallbackUrl = process.env.FI_AGENT_SPRING_URL_CALLBACK ?? 'http://localhost:8080/api/v1/tryon/callback';
  private readonly aiServiceUrl = process.env.AI_SERVICE_URL ?? 'http://localhost:8001/tryon/process';

  async process(job: Job<any, any, string>): Promise<any> {
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
            const aiResult = await aiResponse.json() as any;
            resultImageUrl = aiResult.result_url || resultImageUrl;
            this.logger.log(`AI Service completed for job ${job.id}`);
        } else {
            this.logger.warn(`AI Service returned status ${aiResponse.status}, using default/cached result`);
        }
    } catch (error: unknown) {
        this.logger.error(`Failed to call AI Service: ${(error as Error).message}. Falling back to simulation.`);
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
        } catch (error: unknown) {
            this.logger.error(`Failed to send callback for job ${backendJobId}: ${(error as Error).message}`);
        }
    }

    this.logger.log(`Try-on job ${job.id} completed`);
    return { imageUrl: resultImageUrl };
  }
}