package com.skaly.fashion_backend.testsupport;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

/**
 * Base class for integration tests with PostgreSQL Testcontainer.
 * Provides PostgreSQL with pgvector support for repository/adapter tests.
 */
public abstract class PostgresIntegrationSupport {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg15")
            .withDatabaseName("fashion_test")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void setupProperties() {
        postgres.start();
        
        // Set system properties for Spring to use
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        
        // Disable some features for testing
        System.setProperty("spring.ai.enabled", "false");
        System.setProperty("application.ai.assistant.enabled", "false");
    }
}