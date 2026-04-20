import { Test, TestingModule } from '@nestjs/testing';
import { StrategistService } from './strategist.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { AdminPlan } from './entities/admin-plan.entity';
import { SpringFiAgentGateway } from './spring-fiagent.gateway';
import { LlmClient } from '../common/llm/llm.client';

describe('StrategistService', () => {
  let service: StrategistService;
  let adminPlanRepository: any;
  let llmClient: any;
  let springGateway: any;

  beforeEach(async () => {
    adminPlanRepository = {
      create: jest.fn().mockImplementation((dto) => dto),
      save: jest.fn().mockImplementation((dto) => Promise.resolve({ id: 'uuid-1', ...dto })),
      findOneBy: jest.fn(),
    };

    llmClient = {
      complete: jest.fn().mockResolvedValue('Draft insights from AI'),
    };

    springGateway = {
      calculateBatchScores: jest.fn().mockResolvedValue([]),
    };

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        StrategistService,
        {
          provide: getRepositoryToken(AdminPlan),
          useValue: adminPlanRepository,
        },
        {
          provide: LlmClient,
          useValue: llmClient,
        },
        {
          provide: SpringFiAgentGateway,
          useValue: springGateway,
        },
      ],
    }).compile();

    service = module.get<StrategistService>(StrategistService);
  });

  it('should create a plan in DRAFT status', async () => {
    const goal = 'Increase sales by 20%';
    const result = await service.createPlanFromGoal(goal);

    expect(result.status).toBe('DRAFT');
    expect(adminPlanRepository.save).toHaveBeenCalled();
    expect(llmClient.complete).toHaveBeenCalled();
  });

  it('should approve a draft plan', async () => {
    const draftId = 'uuid-1';
    adminPlanRepository.findOneBy.mockResolvedValue({
      id: draftId,
      status: 'DRAFT',
      draftInsights: 'Original insights',
    });

    const result = await service.reviewDraft({
      draftId,
      decision: 'APPROVE',
      reviewerComment: 'Looks good',
    });

    expect(result.approved).toBe(true);
    expect(adminPlanRepository.save).toHaveBeenCalledWith(
      expect.objectContaining({ status: 'APPROVED' }),
    );
  });
});
