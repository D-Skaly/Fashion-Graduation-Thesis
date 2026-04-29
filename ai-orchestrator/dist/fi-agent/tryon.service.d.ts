import { Queue } from 'bullmq';
export declare class TryOnService {
    private readonly tryOnQueue;
    constructor(tryOnQueue: Queue);
    createTryOnJob(userId: string, productId: string, userImageUrl: string, backendJobId?: string): Promise<{
        jobId: string | undefined;
    }>;
}
