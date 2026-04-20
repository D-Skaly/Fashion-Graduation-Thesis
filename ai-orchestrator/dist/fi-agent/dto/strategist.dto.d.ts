export declare class ProductMetricDto {
    productId: string;
    conversionRate: number;
    gapAnalysisScore: number;
    financeMultiplier: number;
    styleScore: number;
    businessScore: number;
    marketScore: number;
}
export declare class StrategistInsightRequestDto {
    products: ProductMetricDto[];
    wFit: number;
    wPush: number;
    wTrend: number;
    topN: number;
}
export interface StrategistDraftInsightDto {
    draftId: string;
    requiresHumanApproval: boolean;
    confidence: number;
    scoredProducts: Array<{
        productId: string;
        totalScore: number;
    }>;
    draftInsights: string;
}
export declare class StrategistReviewDto {
    draftId: string;
    decision: 'APPROVE' | 'REJECT';
    reviewerComment?: string;
}
export interface StrategistFinalInsightDto {
    approved: boolean;
    finalInsights: string;
}
