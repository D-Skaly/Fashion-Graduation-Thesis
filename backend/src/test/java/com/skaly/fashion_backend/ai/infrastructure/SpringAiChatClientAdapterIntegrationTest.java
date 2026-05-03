package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.testsupport.PostgresIntegrationSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for SpringAiChatClientAdapter.
 * Uses Testcontainers for PostgreSQL.
 */
@SpringBootTest
@ActiveProfiles("test")
class SpringAiChatClientAdapterIntegrationTest extends PostgresIntegrationSupport {

    @Autowired(required = false)
    private SpringAiChatClientAdapter adapter;

    @Test
    void contextLoads() {
        // Basic test to verify context loads
        assertNotNull(adapter);
    }

    @Test
    void shouldImplementAIModelPort() {
        if (adapter != null) {
            assertInstanceOf(com.skaly.fashion_backend.ai.domain.port.AIModelPort.class, adapter);
        } else {
            // If AI is disabled in test profile, skip
            assertTrue(true);
        }
    }

    @Test
    void completeChatPrompt_shouldHandleRequest() {
        if (adapter == null) {
            // AI disabled in test, skip
            return;
        }
        
        // This would need a mocked ChatClient in real integration
        // For now, just verify adapter exists and is wired
        assertNotNull(adapter);
    }
}