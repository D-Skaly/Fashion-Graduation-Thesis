import { Module } from '@nestjs/common';
import { LlmClient } from './common/llm/llm.client';
import { StylistController } from './fi-agent/stylist.controller';
import { StylistService } from './fi-agent/stylist.service';
import { StrategistController } from './fi-agent/strategist.controller';
import { StrategistService } from './fi-agent/strategist.service';
import { SpringFiAgentGateway } from './fi-agent/spring-fiagent.gateway';

@Module({
  controllers: [StylistController, StrategistController],
  providers: [LlmClient, SpringFiAgentGateway, StylistService, StrategistService],
})
export class AppModule {}
