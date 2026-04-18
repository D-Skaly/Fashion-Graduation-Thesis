package com.skaly.fashion_backend.fiagent.analytics;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnalyticsRepository extends JpaRepository<Analytics, UUID> {
    Optional<Analytics> findTopByProductIdOrderByAnalyticsDateDesc(UUID productId);

    List<Analytics> findByAnalyticsDate(LocalDate analyticsDate);
}
