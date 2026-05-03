package com.skaly.fashion_backend.fiagent.domain.repository;

import com.skaly.fashion_backend.fiagent.domain.model.Analytics;
import java.util.List;
import java.util.UUID;

public interface AnalyticsRepository {
    Analytics findById(UUID id);
    List<Analytics> findByProductId(UUID productId);
    Analytics save(Analytics analytics);
    void deleteById(UUID id);
}
