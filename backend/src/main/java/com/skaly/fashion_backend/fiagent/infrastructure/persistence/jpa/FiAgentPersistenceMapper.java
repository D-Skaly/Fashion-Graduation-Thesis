package com.skaly.fashion_backend.fiagent.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.fiagent.domain.model.Analytics;
import com.skaly.fashion_backend.fiagent.domain.model.CustomerProfile;
import org.springframework.stereotype.Component;

@Component
public class FiAgentPersistenceMapper {

    public Analytics toAnalytics(AnalyticsEntity entity) {
        if (entity == null) return null;
        return Analytics.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .analyticsDate(entity.getAnalyticsDate())
                .conversionRate(entity.getConversionRate())
                .gapAnalysisScore(entity.getGapAnalysisScore())
                .financeMultiplier(entity.getFinanceMultiplier())
                .businessScore(entity.getBusinessScore())
                .marketScore(entity.getMarketScore())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public AnalyticsEntity toAnalyticsEntity(Analytics domain) {
        if (domain == null) return null;
        return AnalyticsEntity.builder()
                .id(domain.getId())
                .productId(domain.getProductId())
                .analyticsDate(domain.getAnalyticsDate())
                .conversionRate(domain.getConversionRate())
                .gapAnalysisScore(domain.getGapAnalysisScore())
                .financeMultiplier(domain.getFinanceMultiplier())
                .businessScore(domain.getBusinessScore())
                .marketScore(domain.getMarketScore())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public CustomerProfile toCustomerProfile(CustomerProfileEntity entity) {
        if (entity == null) return null;
        return CustomerProfile.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .preferredStyle(entity.getPreferredStyle())
                .budgetMin(entity.getBudgetMin())
                .budgetMax(entity.getBudgetMax())
                .wFit(entity.getWFit())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .stylePreferenceVectorStr(entity.getStylePreferenceVectorStr())
                .build();
    }

    public CustomerProfileEntity toCustomerProfileEntity(CustomerProfile domain) {
        if (domain == null) return null;
        return CustomerProfileEntity.builder()
                .id(domain.getId())
                .customerId(domain.getCustomerId())
                .preferredStyle(domain.getPreferredStyle())
                .budgetMin(domain.getBudgetMin())
                .budgetMax(domain.getBudgetMax())
                .wFit(domain.getWFit())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .stylePreferenceVectorStr(domain.getStylePreferenceVectorStr())
                .build();
    }
}
