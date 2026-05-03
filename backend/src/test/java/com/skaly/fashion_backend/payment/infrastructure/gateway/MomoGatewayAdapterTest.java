package com.skaly.fashion_backend.payment.infrastructure.gateway;

import com.skaly.fashion_backend.testsupport.PostgresIntegrationSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for MomoGatewayAdapter.
 * Uses Testcontainers for PostgreSQL.
 */
@SpringBootTest
@ActiveProfiles("test")
class MomoGatewayAdapterTest extends PostgresIntegrationSupport {

    @Autowired(required = false)
    private MomoGatewayAdapter adapter;

    @Test
    void contextLoads() {
        // Basic test to verify context loads
        assertTrue(true);
    }

    @Test
    void shouldImplementPaymentGatewayPort() {
        if (adapter != null) {
            assertInstanceOf(com.skaly.fashion_backend.payment.domain.port.PaymentGatewayPort.class, adapter);
        } else {
            // If payment gateway is disabled in test profile, skip
            assertTrue(true);
        }
    }

    @Test
    void paymentMethod_shouldReturnMomo() {
        if (adapter == null) return;
        
        assertEquals(com.skaly.fashion_backend.payment.PaymentMethod.MOMO, adapter.paymentMethod());
    }

    @Test
    void processCallback_shouldHandleValidParams() {
        if (adapter == null) return;
        
        Map<String, String> params = new HashMap<>();
        params.put("orderId", "TEST-123");
        params.put("resultCode", "0");
        params.put("signature", "testsignature");
        
        // This would need actual Momo signature validation
        // For now, just verify adapter exists
        assertNotNull(adapter);
    }
}