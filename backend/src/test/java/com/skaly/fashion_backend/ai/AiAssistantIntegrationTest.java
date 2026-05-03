package com.skaly.fashion_backend.ai;

import com.skaly.fashion_backend.ai.domain.port.AIModelPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skaly.fashion_backend.common.GlobalExceptionHandler;
import com.skaly.fashion_backend.recommendation.application.RecommendProductInteractor;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AiAssistantIntegrationTest {

    @Test
    void postChat_ShouldReturnAiAnswer_WhenChatClientIsAvailable() throws Exception {
        AIModelPort port = mock(AIModelPort.class);
        when(port.completeChatPrompt(anyString())).thenReturn("Gợi ý: Áo blazer + quần tây đen");

        AiAssistantProperties properties = new AiAssistantProperties(
                true,
                1000,
                new AiAssistantProperties.Retry(3, 10),
                new AiAssistantProperties.Timeout(2000),
                new AiAssistantProperties.RateLimit(true, 20, 60));

        ChatSessionService chatSessionService = mock(ChatSessionService.class);
        FashionAssistantService service = new FashionAssistantService(port, properties, new SimpleMeterRegistry(), chatSessionService);
        SizeRecommendationService sizeRecommendationService = mock(SizeRecommendationService.class);
        RecommendProductInteractor recommendProductInteractor = mock(RecommendProductInteractor.class);
        AiController controller = new AiController(service, sizeRecommendationService, recommendProductInteractor);
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
        AIModelPort port = mock(AIModelPort.class);
        when(port.completeChatPrompt(anyString())).thenReturn("Answer");

        AiAssistantProperties properties = new AiAssistantProperties(true, 1000, new AiAssistantProperties.Retry(1, 1), new AiAssistantProperties.Timeout(1000), new AiAssistantProperties.RateLimit(false, 0, 0));
        
        ChatSessionService chatSessionService = mock(ChatSessionService.class);
        FashionAssistantService service = new FashionAssistantService(port, properties, new SimpleMeterRegistry(), chatSessionService);

        service.chat("shirt");
        
        verify(port).completeChatPrompt(anyString());
    }
}
