package com.skaly.fashion_backend.ai;

import com.skaly.fashion_backend.product.application.ProductEmbeddingService;
import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.recommendation.domain.port.AIModelPort;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FashionAssistantServiceUnitTest {

    private AIModelPort aiModelPort;
    private AiAssistantProperties properties;
    private MeterRegistry meterRegistry;
    private ProductEmbeddingService productEmbeddingService;
    private ProductRepository productRepository;
    private ChatSessionService chatSessionService;
    private FashionAssistantService fashionAssistantService;

    @BeforeEach
    void setUp() {
        aiModelPort = mock(AIModelPort.class);
        properties = mock(AiAssistantProperties.class);
        meterRegistry = new SimpleMeterRegistry();
        productEmbeddingService = mock(ProductEmbeddingService.class);
        productRepository = mock(ProductRepository.class);
        chatSessionService = mock(ChatSessionService.class);

        when(properties.enabled()).thenReturn(true);
        when(properties.maxMessageLength()).thenReturn(1000);

        fashionAssistantService = new FashionAssistantService(
                aiModelPort,
                properties,
                meterRegistry,
                productEmbeddingService,
                productRepository,
                chatSessionService
        );
    }

    @Test
    void shouldCallAIModelWhenRecommendationIsRequested() {
        String userMessage = "Gợi ý cho tôi váy hoa";
        float[] dummyVector = new float[]{0.1f, 0.2f};
        when(productEmbeddingService.embedQuery(anyString())).thenReturn(dummyVector);
        when(productRepository.findTopKByEmbeddingVectorClosestTo(any(), anyInt())).thenReturn(Collections.emptyList());
        when(aiModelPort.completeChatPrompt(anyString())).thenReturn("Đây là gợi ý váy hoa của tôi.");

        String result = fashionAssistantService.chat(userMessage);

        assertEquals("Đây là gợi ý váy hoa của tôi.", result);
        verify(productEmbeddingService).embedQuery(contains("váy hoa"));
        verify(aiModelPort).completeChatPrompt(contains("Yêu cầu hiện tại: Gợi ý cho tôi váy hoa"));
    }

    @Test
    void shouldIncludeProductContextInPromptWhenProductsFound() {
        String userMessage = "Tìm áo thun";
        float[] dummyVector = new float[]{0.1f, 0.2f};
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Áo thun basic")
                .basePrice(new BigDecimal("200000"))
                .description("Áo thun cotton 100%")
                .build();

        when(productEmbeddingService.embedQuery(anyString())).thenReturn(dummyVector);
        when(productRepository.findTopKByEmbeddingVectorClosestTo(eq(dummyVector), anyInt()))
                .thenReturn(Collections.singletonList(product));
        when(aiModelPort.completeChatPrompt(anyString())).thenAnswer(invocation -> {
            String prompt = invocation.getArgument(0);
            assertTrue(prompt.contains("Thông tin sản phẩm có sẵn:"));
            assertTrue(prompt.contains("Áo thun basic"));
            return "Tôi đã tìm thấy áo thun cho bạn.";
        });

        fashionAssistantService.chat(userMessage);

        verify(aiModelPort).completeChatPrompt(anyString());
    }
}
