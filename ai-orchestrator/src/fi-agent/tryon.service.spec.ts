import { Test, TestingModule } from '@nestjs/testing';
import { TryOnService } from './tryon.service';
import { getQueueToken } from '@nestjs/bullmq';

describe('TryOnService', () => {
  let service: TryOnService;
  let queue: any;

  beforeEach(async () => {
    queue = {
      add: jest.fn().mockResolvedValue({ id: 'job-1' }),
    };

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        TryOnService,
        {
          provide: getQueueToken('virtual-try-on'),
          useValue: queue,
        },
      ],
    }).compile();

    service = module.get<TryOnService>(TryOnService);
  });

  it('should add a try-on job to the queue and return job ID', async () => {
    const result = await service.createTryOnJob('user-1', 'product-1', 'http://image.url');

    expect(result).toEqual({ jobId: 'job-1' });
    expect(queue.add).toHaveBeenCalledWith(
      'process-try-on',
      expect.objectContaining({
        userId: 'user-1',
        productId: 'product-1',
        userImageUrl: 'http://image.url',
      }),
      expect.any(Object),
    );
  });
});
