import { StrategistDraftInsightDto, StrategistFinalInsightDto, StrategistInsightRequestDto, StrategistReviewDto } from './dto/strategist.dto';
import { StrategistService } from './strategist.service';
export declare class StrategistController {
    private readonly strategistService;
    constructor(strategistService: StrategistService);
    generateDraft(body: StrategistInsightRequestDto): Promise<StrategistDraftInsightDto>;
    reviewDraft(body: StrategistReviewDto): Promise<StrategistFinalInsightDto>;
    createPlan(body: {
        goal: string;
    }): Promise<import("./entities/admin-plan.entity").AdminPlan>;
}
