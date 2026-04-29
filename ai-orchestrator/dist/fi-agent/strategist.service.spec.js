"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const testing_1 = require("@nestjs/testing");
const strategist_service_1 = require("./strategist.service");
const typeorm_1 = require("@nestjs/typeorm");
const admin_plan_entity_1 = require("./entities/admin-plan.entity");
const spring_fiagent_gateway_1 = require("./spring-fiagent.gateway");
const llm_client_1 = require("../common/llm/llm.client");
describe('StrategistService', () => {
    let service;
    let adminPlanRepository;
    let llmClient;
    let springGateway;
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
        const module = await testing_1.Test.createTestingModule({
            providers: [
                strategist_service_1.StrategistService,
                {
                    provide: (0, typeorm_1.getRepositoryToken)(admin_plan_entity_1.AdminPlan),
                    useValue: adminPlanRepository,
                },
                {
                    provide: llm_client_1.LlmClient,
                    useValue: llmClient,
                },
                {
                    provide: spring_fiagent_gateway_1.SpringFiAgentGateway,
                    useValue: springGateway,
                },
            ],
        }).compile();
        service = module.get(strategist_service_1.StrategistService);
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
        expect(adminPlanRepository.save).toHaveBeenCalledWith(expect.objectContaining({ status: 'APPROVED' }));
    });
});
//# sourceMappingURL=strategist.service.spec.js.map