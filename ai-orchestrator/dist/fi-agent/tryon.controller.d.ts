import { TryOnService } from './tryon.service';
export declare class TryOnController {
    private readonly tryOnService;
    constructor(tryOnService: TryOnService);
    createJob(body: {
        userId: string;
        productId: string;
        userImageUrl: string;
        jobId: string;
    }): Promise<{
        jobId: string | undefined;
    }>;
}
