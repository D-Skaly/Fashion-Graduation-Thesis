import { Injectable, InternalServerErrorException } from '@nestjs/common';

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
@Injectable()
export class SpringFiAgentGateway {
  private readonly springBaseUrl =
    process.env.FI_AGENT_SPRING_URL ?? 'http://localhost:8080/api/v1/fi-agent';

  async getTopProductsByStyleVector(styleVector: number[], limit: number): Promise<ProductCandidate[]> {
    const response = await fetch(`${this.springBaseUrl}/stylist/top-products`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ styleVector, limit }),
    });

    if (!response.ok) {
      throw new InternalServerErrorException('Failed to retrieve top products from Spring backend.');
    }

    return (await response.json()) as ProductCandidate[];
  }

  async calculateBatchScores(payload: {
    weights: { wFit: number; wPush: number; wTrend: number };
    products: Array<{
      productId: string;
      styleScore: number;
      financeMultiplier: number;
      businessScore: number;
      marketScore: number;
    }>;
  }): Promise<ScoredProduct[]> {
    const response = await fetch(`${this.springBaseUrl}/scoring/batch`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      throw new InternalServerErrorException('Failed to compute FI-Agent scores in Spring backend.');
    }

    return (await response.json()) as ScoredProduct[];
  }
}
