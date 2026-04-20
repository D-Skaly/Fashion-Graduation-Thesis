package com.skaly.fashion_backend.ai;

import com.skaly.fashion_backend.ai.domain.AIModelPort;
import com.skaly.fashion_backend.product.ProductEmbeddingService;
import com.skaly.fashion_backend.product.ProductEntity;
import com.skaly.fashion_backend.product.ProductRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        // Arrange
        String userMessage = "Gợi ý cho tôi váy hoa";
        float[] dummyVector = new float[]{0.1f, 0.2f};
        when(productEmbeddingService.embedQuery(anyString())).thenReturn(dummyVector);
        when(productRepository.findTopKByEmbeddingVectorClosestTo(any(), anyInt())).thenReturn(Collections.emptyList());
        when(aiModelPort.generateResponse(anyString())).thenReturn("Đây là gợi ý váy hoa của tôi.");

        // Act
        String result = fashionAssistantService.chat(userMessage);

        // Assert
        assertEquals("Đây là gợi ý váy hoa của tôi.", result);
        verify(productEmbeddingService).embedQuery(contains("váy hoa"));
        verify(aiModelPort).generateResponse(contains("Yêu cầu hiện tại: Gợi ý cho tôi váy hoa"));
    }

    @Test
    void shouldIncludeProductContextInPromptWhenProductsFound() {
        // Arrange
        String userMessage = "Tìm áo thun";
        float[] dummyVector = new float[]{0.1f, 0.2f};
        var product = mock(ProductEntity.class);
        when(product.getName()).thenReturn("Áo thun basic");
        when(product.getBasePrice()).thenReturn(new java.math.BigDecimal("200000"));
        when(product.getDescription()).thenReturn("Áo thun cotton 100%");

        when(productEmbeddingService.embedQuery(anyString())).thenReturn(dummyVector);
        when(productRepository.findTopKByEmbeddingVectorClosestTo(eq(dummyVector), anyInt()))
                .thenReturn(Collections.singletonList(product));
        when(aiModelPort.generateResponse(anyString())).thenAnswer(invocation -> {
            String prompt = invocation.getArgument(0);
            assertTrue(prompt.contains("Thông tin sản phẩm có sẵn:"));
            assertTrue(prompt.contains("Áo thun basic"));
            return "Tôi đã tìm thấy áo thun cho bạn.";
        });

        // Act
        fashionAssistantService.chat(userMessage);

        // Assert
        verify(aiModelPort).generateResponse(anyString());
    }
}
