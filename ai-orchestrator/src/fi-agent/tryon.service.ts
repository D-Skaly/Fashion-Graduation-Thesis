import { Injectable } from '@nestjs/common';
import { InjectQueue } from '@nestjs/bullmq';
import { Queue } from 'bullmq';

@Injectable()
export class TryOnService {
  constructor(
    @InjectQueue('virtual-try-on') 
    private readonly tryOnQueue: Queue,
  ) {}

  async createTryOnJob(userId: string, productId: string, userImageUrl: string, backendJobId?: string) {
    const job = await this.tryOnQueue.add('process-try-on', {
      userId,
      productId,
      userImageUrl,
      backendJobId,
      timestamp: new Date().toISOString(),
    }, {
      attempts: 3,
      backoff: {
        type: 'exponential',
        delay: 5000,
      },
    });

    return { jobId: job.id };
  }
}
