import { Controller, Post, Body } from '@nestjs/common';
import { TryOnService } from './tryon.service';

@Controller('api/v1/ai/tryon')
export class TryOnController {
  constructor(private readonly tryOnService: TryOnService) {}

  @Post()
  async createJob(
    @Body() body: { userId: string; productId: string; userImageUrl: string; jobId: string },
  ) {
    return await this.tryOnService.createTryOnJob(
      body.userId,
      body.productId,
      body.userImageUrl,
      body.jobId,
    );
  }
}
