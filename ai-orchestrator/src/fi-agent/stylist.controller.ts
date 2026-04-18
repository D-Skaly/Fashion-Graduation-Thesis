import { Body, Controller, Post } from '@nestjs/common';
import { StylistAdviceRequestDto, StylistAdviceResponseDto } from './dto/stylist.dto';
import { StylistService } from './stylist.service';

@Controller('stylist')
export class StylistController {
  constructor(private readonly stylistService: StylistService) {}

  @Post('advice')
  async generateAdvice(@Body() body: StylistAdviceRequestDto): Promise<StylistAdviceResponseDto> {
    return this.stylistService.generateAdvice(body);
  }
}
