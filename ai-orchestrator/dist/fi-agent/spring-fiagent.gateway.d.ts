export interface ProductCandidate {
    id: string;
    name: string;
    brand: string | null;
    basePrice: number;
}
export interface ScoredProduct {
    productId: string;
    totalScore: number;
}
/**
 * Gateway for FI-Agent Spring backend endpoints.
 */
export declare class SpringFiAgentGateway {
    private readonly springBaseUrl;
    getTopProductsByStyleVector(styleVector: number[], limit: number): Promise<ProductCandidate[]>;
    calculateBatchScores(payload: {
        weights: {
            wFit: number;
            wPush: number;
            wTrend: number;
        };
        products: Array<{
            productId: string;
            styleScore: number;
            financeMultiplier: number;
            businessScore: number;
            marketScore: number;
        }>;
    }): Promise<ScoredProduct[]>;
}
