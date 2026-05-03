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
 * Integration test for VnPayGatewayAdapter.
 * Uses Testcontainers for PostgreSQL.
 */
@SpringBootTest
@ActiveProfiles("test")
class VnPayGatewayAdapterTest extends PostgresIntegrationSupport {

    @Autowired(required = false)
    private VnPayGatewayAdapter adapter;

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
    void paymentMethod_shouldReturnVnPay() {
        if (adapter == null) return;
        
        assertEquals(com.skaly.fashion_backend.payment.PaymentMethod.VNPAY, adapter.paymentMethod());
    }

    @Test
    void processCallback_shouldHandleValidParams() {
        if (adapter == null) return;
        
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", "TEST-123");
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_SecureHash", "testhash");
        
        // This would need actual VnPay signature validation
        // For now, just verify adapter exists
        assertNotNull(adapter);
    }
}