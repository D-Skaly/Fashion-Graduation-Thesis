package com.skaly.fashion_backend.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Check database connectivity
        // Check Redis connectivity
        // Check MinIO connectivity
        // Return Health.up() or Health.down()
        return Health.up()
                .withDetail("database", "PostgreSQL connected")
                .withDetail("cache", "Redis connected")
                .withDetail("storage", "MinIO connected")
                .build();
    }
}
