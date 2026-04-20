export declare class StylistAdviceRequestDto {
    styleVector: number[];
    customerContext?: string;
    limit: number;
}
export interface StylistAdviceResponseDto {
    recommendedProducts: Array<{
        id: string;
        name: string;
        brand: string | null;
        basePrice: number;
    }>;
    advice: string;
}
