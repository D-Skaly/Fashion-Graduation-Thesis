import { StylistAdviceRequestDto, StylistAdviceResponseDto } from './dto/stylist.dto';
import { StylistService } from './stylist.service';
export declare class StylistController {
    private readonly stylistService;
    constructor(stylistService: StylistService);
    generateAdvice(body: StylistAdviceRequestDto): Promise<StylistAdviceResponseDto>;
}
