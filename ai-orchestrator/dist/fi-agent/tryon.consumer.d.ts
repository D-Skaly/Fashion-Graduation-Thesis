import { WorkerHost } from '@nestjs/bullmq';
import { Job } from 'bullmq';
export declare class TryOnConsumer extends WorkerHost {
    private readonly logger;
    private readonly springCallbackUrl;
    private readonly aiServiceUrl;
    process(job: Job<any, any, string>): Promise<any>;
}
