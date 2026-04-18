import { ArrayMaxSize, ArrayMinSize, IsArray, IsInt, IsNumber, IsOptional, IsString, Max, Min } from 'class-validator';

export class StylistAdviceRequestDto {
  @IsArray()
  @ArrayMinSize(8)
  @ArrayMaxSize(1536)
  @IsNumber({}, { each: true })
  styleVector!: number[];

  @IsOptional()
  @IsString()
  customerContext?: string;

  @IsOptional()
  @IsInt()
  @Min(1)
  @Max(30)
  limit = 10;
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
