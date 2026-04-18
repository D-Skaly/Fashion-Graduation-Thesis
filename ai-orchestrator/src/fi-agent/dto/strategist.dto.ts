import { IsArray, IsInt, IsNumber, IsOptional, IsString, Max, Min, ValidateNested } from 'class-validator';
import { Type } from 'class-transformer';

export class ProductMetricDto {
  @IsString()
  productId!: string;

  @IsNumber()
  @Min(0)
  @Max(1)
  conversionRate!: number;

  @IsNumber()
  @Min(0)
  @Max(1)
  gapAnalysisScore!: number;

  @IsNumber()
  @Min(0)
  financeMultiplier!: number;

  @IsNumber()
  @Min(0)
  @Max(1)
  styleScore!: number;

  @IsNumber()
  @Min(0)
  @Max(1)
  businessScore!: number;

  @IsNumber()
  @Min(0)
  @Max(1)
  marketScore!: number;
}

export class StrategistInsightRequestDto {
  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => ProductMetricDto)
  products!: ProductMetricDto[];

  @IsOptional()
  @IsNumber()
  @Min(0)
  wFit = 1.0;

  @IsOptional()
  @IsNumber()
  wPush = 0.5;

  @IsOptional()
  @IsNumber()
  wTrend = 0.5;

  @IsOptional()
  @IsInt()
  @Min(1)
  @Max(20)
  topN = 5;
}

export interface StrategistDraftInsightDto {
  draftId: string;
  requiresHumanApproval: boolean;
  confidence: number;
  scoredProducts: Array<{ productId: string; totalScore: number }>;
  draftInsights: string;
}

export class StrategistReviewDto {
  @IsString()
  draftId!: string;

  @IsString()
  decision!: 'APPROVE' | 'REJECT';

  @IsOptional()
  @IsString()
  reviewerComment?: string;
}

export interface StrategistFinalInsightDto {
  approved: boolean;
  finalInsights: string;
}
