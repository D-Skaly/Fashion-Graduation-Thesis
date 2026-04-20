import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { BullModule } from '@nestjs/bullmq';
import { LLM_PROVIDER } from './common/llm/llm-provider.interface';
import { OpenAiAdapter } from './common/llm/openai.adapter';
import { StylistController } from './fi-agent/stylist.controller';
import { StylistService } from './fi-agent/stylist.service';
import { StrategistController } from './fi-agent/strategist.controller';
import { StrategistService } from './fi-agent/strategist.service';
import { TryOnService } from './fi-agent/tryon.service';
import { TryOnConsumer } from './fi-agent/tryon.consumer';
import { SpringFiAgentGateway } from './fi-agent/spring-fiagent.gateway';
import { HealthController } from './health/health.controller';
import { AdminPlan } from './fi-agent/entities/admin-plan.entity';

import { TryOnController } from './fi-agent/tryon.controller';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'sqlite',
      database: 'fi_agent.sqlite',
      entities: [AdminPlan],
      synchronize: true, // Only for development as per guidelines
    }),
    TypeOrmModule.forFeature([AdminPlan]),
    BullModule.forRoot({
      connection: {
        host: process.env.REDIS_HOST || 'localhost',
        port: parseInt(process.env.REDIS_PORT || '6379'),
      },
    }),
    BullModule.registerQueue({
      name: 'virtual-try-on',
    }),
  ],
  controllers: [StylistController, StrategistController, TryOnController, HealthController],
  providers: [
    {
      provide: LLM_PROVIDER,
      useClass: OpenAiAdapter,
    },
    SpringFiAgentGateway,
    StylistService,
    StrategistService,
    TryOnService,
    TryOnConsumer,
  ],
})
export class AppModule {}
