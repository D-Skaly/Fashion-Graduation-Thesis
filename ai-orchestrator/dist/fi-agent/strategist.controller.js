"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.StrategistController = void 0;
const common_1 = require("@nestjs/common");
const strategist_dto_1 = require("./dto/strategist.dto");
const strategist_service_1 = require("./strategist.service");
let StrategistController = class StrategistController {
    strategistService;
    constructor(strategistService) {
        this.strategistService = strategistService;
    }
    async generateDraft(body) {
        return this.strategistService.generateDraftInsights(body);
    }
    async reviewDraft(body) {
        return this.strategistService.reviewDraft(body);
    }
    async createPlan(body) {
        return this.strategistService.createPlanFromGoal(body.goal);
    }
};
exports.StrategistController = StrategistController;
__decorate([
    (0, common_1.Post)('insights/draft'),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [strategist_dto_1.StrategistInsightRequestDto]),
    __metadata("design:returntype", Promise)
], StrategistController.prototype, "generateDraft", null);
__decorate([
    (0, common_1.Post)('insights/review'),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [strategist_dto_1.StrategistReviewDto]),
    __metadata("design:returntype", Promise)
], StrategistController.prototype, "reviewDraft", null);
__decorate([
    (0, common_1.Post)('plan'),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], StrategistController.prototype, "createPlan", null);
exports.StrategistController = StrategistController = __decorate([
    (0, common_1.Controller)('strategist'),
    __metadata("design:paramtypes", [strategist_service_1.StrategistService])
], StrategistController);
//# sourceMappingURL=strategist.controller.js.map