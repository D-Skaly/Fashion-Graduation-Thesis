package com.skaly.fashion_backend.ai;

import com.skaly.fashion_backend.ai.domain.port.AIModelPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FashionAssistantServiceUnitTest {

    private AIModelPort aiModelPort;
    private AiAssistantProperties properties;
    private MeterRegistry meterRegistry;
    private ChatSessionService chatSessionService;
    private FashionAssistantService fashionAssistantService;

    @BeforeEach
    void setUp() {
        aiModelPort = mock(AIModelPort.class);
        properties = mock(AiAssistantProperties.class);
        meterRegistry = new SimpleMeterRegistry();
        chatSessionService = mock(ChatSessionService.class);

        when(properties.enabled()).thenReturn(true);
        when(properties.maxMessageLength()).thenReturn(1000);

        fashionAssistantService = new FashionAssistantService(
                aiModelPort,
                properties,
                meterRegistry,
                chatSessionService
        );
    }

    @Test
    void shouldCallAIModelWhenRecommendationIsRequested() {
        String userMessage = "Gợi ý cho tôi váy hoa";
        when(aiModelPort.completeChatPrompt(anyString())).thenReturn("Đây là gợi ý váy hoa của tôi.");

        String result = fashionAssistantService.chat(userMessage);

        assertEquals("Đây là gợi ý váy hoa của tôi.", result);
        verify(aiModelPort).completeChatPrompt(contains("Yêu cầu hiện tại: Gợi ý cho tôi váy hoa"));
    }

    @Test
    void shouldCallAIModelWithUserMessage() {
        String userMessage = "Tìm áo thun";
        when(aiModelPort.completeChatPrompt(anyString())).thenAnswer(invocation -> {
            String prompt = invocation.getArgument(0);
            assertTrue(prompt.contains("Yêu cầu hiện tại: Tìm áo thun"));
            return "Tôi đã tìm thấy áo thun cho bạn.";
        });

        fashionAssistantService.chat(userMessage);

        verify(aiModelPort).completeChatPrompt(anyString());
    }
}
