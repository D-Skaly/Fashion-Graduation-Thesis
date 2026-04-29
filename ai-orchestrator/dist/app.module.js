"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.AppModule = void 0;
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const bullmq_1 = require("@nestjs/bullmq");
const llm_provider_interface_1 = require("./common/llm/llm-provider.interface");
const openai_adapter_1 = require("./common/llm/openai.adapter");
const stylist_controller_1 = require("./fi-agent/stylist.controller");
const stylist_service_1 = require("./fi-agent/stylist.service");
const strategist_controller_1 = require("./fi-agent/strategist.controller");
const strategist_service_1 = require("./fi-agent/strategist.service");
const tryon_service_1 = require("./fi-agent/tryon.service");
const tryon_consumer_1 = require("./fi-agent/tryon.consumer");
const spring_fiagent_gateway_1 = require("./fi-agent/spring-fiagent.gateway");
const health_controller_1 = require("./health/health.controller");
const admin_plan_entity_1 = require("./fi-agent/entities/admin-plan.entity");
const tryon_controller_1 = require("./fi-agent/tryon.controller");
let AppModule = class AppModule {
};
exports.AppModule = AppModule;
exports.AppModule = AppModule = __decorate([
    (0, common_1.Module)({
        imports: [
            typeorm_1.TypeOrmModule.forRoot({
                type: 'sqlite',
                database: 'fi_agent.sqlite',
                entities: [admin_plan_entity_1.AdminPlan],
                synchronize: true, // Only for development as per guidelines
            }),
            typeorm_1.TypeOrmModule.forFeature([admin_plan_entity_1.AdminPlan]),
            bullmq_1.BullModule.forRoot({
                connection: {
                    host: process.env.REDIS_HOST || 'localhost',
                    port: parseInt(process.env.REDIS_PORT || '6379'),
                },
            }),
            bullmq_1.BullModule.registerQueue({
                name: 'virtual-try-on',
            }),
        ],
        controllers: [stylist_controller_1.StylistController, strategist_controller_1.StrategistController, tryon_controller_1.TryOnController, health_controller_1.HealthController],
        providers: [
            {
                provide: llm_provider_interface_1.LLM_PROVIDER,
                useClass: openai_adapter_1.OpenAiAdapter,
            },
            spring_fiagent_gateway_1.SpringFiAgentGateway,
            stylist_service_1.StylistService,
            strategist_service_1.StrategistService,
            tryon_service_1.TryOnService,
            tryon_consumer_1.TryOnConsumer,
        ],
    })
], AppModule);
//# sourceMappingURL=app.module.js.map