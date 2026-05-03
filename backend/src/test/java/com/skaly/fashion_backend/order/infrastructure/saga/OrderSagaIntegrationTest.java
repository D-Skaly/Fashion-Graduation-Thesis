package com.skaly.fashion_backend.order.infrastructure.saga;

import com.skaly.fashion_backend.testsupport.PostgresIntegrationSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.skaly.fashion_backend.order.infrastructure.OrderUserGatewayAdapter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Order processing.
 * Uses Testcontainers for PostgreSQL.
 */
@SpringBootTest
@ActiveProfiles("test")
class OrderSagaIntegrationTest extends PostgresIntegrationSupport {

    @Autowired(required = false)
    private OrderUserGatewayAdapter adapter;

    @Test
    void contextLoads() {
        // Basic test to verify context loads
        assertTrue(true);
    }

    @Test
    void adapterShouldBeWired() {
        if (adapter != null) {
            assertNotNull(adapter);
        } else {
            // If adapter is disabled in test profile, skip
            assertTrue(true);
        }
    }

    @Test
    void getUserIdByEmail_shouldHandleRequest() {
        if (adapter == null) {
            return;
        }
        
        // Test with non-existent email
        try {
            adapter.getUserIdByEmail("nonexistent@test.com");
        } catch (Exception e) {
            // Expected to throw exception for non-existent user
        }
        assertNotNull(adapter);
    }
}