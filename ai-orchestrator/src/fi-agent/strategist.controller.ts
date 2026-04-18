import { Body, Controller, Post } from '@nestjs/common';
import {
  StrategistDraftInsightDto,
  StrategistFinalInsightDto,
  StrategistInsightRequestDto,
  StrategistReviewDto,
} from './dto/strategist.dto';
import { StrategistService } from './strategist.service';

@Controller('strategist')
export class StrategistController {
  constructor(private readonly strategistService: StrategistService) {}

  @Post('insights/draft')
  async generateDraft(
    @Body() body: StrategistInsightRequestDto,
  ): Promise<StrategistDraftInsightDto> {
    return this.strategistService.generateDraftInsights(body);
  }

  @Post('insights/review')
  async reviewDraft(@Body() body: StrategistReviewDto): Promise<StrategistFinalInsightDto> {
    return this.strategistService.reviewDraft(body);
  }
}
