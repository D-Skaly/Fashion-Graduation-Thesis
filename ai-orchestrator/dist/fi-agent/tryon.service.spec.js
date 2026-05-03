"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const testing_1 = require("@nestjs/testing");
const tryon_service_1 = require("./tryon.service");
const bullmq_1 = require("@nestjs/bullmq");
describe('TryOnService', () => {
    let service;
    let queue;
    beforeEach(async () => {
        queue = {
            add: jest.fn().mockResolvedValue({ id: 'job-1' }),
        };
        const module = await testing_1.Test.createTestingModule({
            providers: [
                tryon_service_1.TryOnService,
                {
                    provide: (0, bullmq_1.getQueueToken)('virtual-try-on'),
                    useValue: queue,
                },
            ],
        }).compile();
        service = module.get(tryon_service_1.TryOnService);
    });
    it('should add a try-on job to the queue and return job ID', async () => {
        const result = await service.createTryOnJob('user-1', 'product-1', 'http://image.url');
        expect(result).toEqual({ jobId: 'job-1' });
        expect(queue.add).toHaveBeenCalledWith('process-try-on', expect.objectContaining({
            userId: 'user-1',
            productId: 'product-1',
            userImageUrl: 'http://image.url',
        }), expect.any(Object));
    });
});
//# sourceMappingURL=tryon.service.spec.js.map