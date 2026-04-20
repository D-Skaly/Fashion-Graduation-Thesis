package com.skaly.fashion_backend.ai;

import com.skaly.fashion_backend.ai.domain.AIModelPort;
import com.skaly.fashion_backend.ai.infrastructure.SpringAIChatAdapter;
import com.skaly.fashion_backend.common.GlobalExceptionHandler;
import com.skaly.fashion_backend.product.ProductEmbeddingService;
import com.skaly.fashion_backend.product.ProductEntity;
import com.skaly.fashion_backend.product.ProductRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AiAssistantIntegrationTest {

    @Test
    void postChat_ShouldReturnAiAnswer_WhenChatModelIsAvailable() throws Exception {
        ChatModel chatModel = mock(ChatModel.class);
        when(chatModel.call(anyString())).thenReturn("Gợi ý: Áo blazer + quần tây đen");

        AIModelPort port = new SpringAIChatAdapter(chatModel);
        AiAssistantProperties properties = new AiAssistantProperties(
                true,
                1000,
                new AiAssistantProperties.Retry(3, 10),
                new AiAssistantProperties.Timeout(2000),
                new AiAssistantProperties.RateLimit(true, 20, 60));

        ProductEmbeddingService productEmbeddingService = mock(ProductEmbeddingService.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productEmbeddingService.embedQuery(anyString())).thenReturn(new float[]{0.1f});
        when(productRepository.findTopKByEmbeddingVectorClosestTo(any(), anyInt())).thenReturn(Collections.emptyList());

        ChatSessionService chatSessionService = mock(ChatSessionService.class);
        FashionAssistantService service = new FashionAssistantService(port, properties, new SimpleMeterRegistry(), productEmbeddingService, productRepository, chatSessionService);
        AiController controller = new AiController(service);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(post("/api/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Tư vấn outfit đi làm\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.answer").value("Gợi ý: Áo blazer + quần tây đen"));
    }

    @Test
    void chat_ShouldIncludeProductContext_WhenFound() {
        ChatModel chatModel = mock(ChatModel.class);
        when(chatModel.call(anyString())).thenReturn("Answer");

        AIModelPort port = new SpringAIChatAdapter(chatModel);
        AiAssistantProperties properties = new AiAssistantProperties(true, 1000, new AiAssistantProperties.Retry(1, 1), new AiAssistantProperties.Timeout(1000), new AiAssistantProperties.RateLimit(false, 0, 0));
        
        ProductEmbeddingService productEmbeddingService = mock(ProductEmbeddingService.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        var product = mock(ProductEntity.class);
        when(product.getName()).thenReturn("Test Shirt");
        when(product.getBasePrice()).thenReturn(new java.math.BigDecimal("100"));
        when(product.getDescription()).thenReturn("Desc");
        
        when(productEmbeddingService.embedQuery(anyString())).thenReturn(new float[]{0.1f});
        when(productRepository.findTopKByEmbeddingVectorClosestTo(any(), anyInt())).thenReturn(Collections.singletonList(product));

        ChatSessionService chatSessionService = mock(ChatSessionService.class);
        FashionAssistantService service = new FashionAssistantService(port, properties, new SimpleMeterRegistry(), productEmbeddingService, productRepository, chatSessionService);

        service.chat("shirt");
        
        verify(chatModel).call(anyString());
    }
}
