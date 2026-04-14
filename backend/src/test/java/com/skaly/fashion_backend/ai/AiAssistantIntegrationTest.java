package com.skaly.fashion_backend.ai;

import com.skaly.fashion_backend.common.GlobalExceptionHandler;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AiAssistantIntegrationTest {

    @Test
    void postChat_ShouldReturnAiAnswer_WhenChatModelIsAvailable() throws Exception {
        ChatModel chatModel = mock(ChatModel.class);
        when(chatModel.call(anyString())).thenReturn("Gợi ý: Áo blazer + quần tây đen");

        ObjectProvider<ChatModel> provider = () -> chatModel;
        AiAssistantProperties properties = new AiAssistantProperties(
                true,
                1000,
                new AiAssistantProperties.Retry(3, 10),
                new AiAssistantProperties.Timeout(2000),
                new AiAssistantProperties.RateLimit(true, 20, 60));

        FashionAssistantService service = new FashionAssistantService(provider, properties, new SimpleMeterRegistry());
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
    void chat_ShouldRetryAndSucceed_OnTransientFailure() {
        ChatModel chatModel = mock(ChatModel.class);
        final int[] calls = { 0 };

        doAnswer(invocation -> {
            calls[0]++;
            if (calls[0] < 2) {
                throw new RuntimeException("Transient error");
            }
            return "Đầm đen tối giản + phụ kiện bạc";
        }).when(chatModel).call(anyString());

        ObjectProvider<ChatModel> provider = () -> chatModel;
        AiAssistantProperties properties = new AiAssistantProperties(
                true,
                1000,
                new AiAssistantProperties.Retry(3, 10),
                new AiAssistantProperties.Timeout(2000),
                new AiAssistantProperties.RateLimit(true, 20, 60));

        FashionAssistantService service = new FashionAssistantService(provider, properties, new SimpleMeterRegistry());

        String response = service.chat("Tư vấn đi tiệc");
        org.assertj.core.api.Assertions.assertThat(response).contains("Đầm đen");
    }

    @Test
    void chat_ShouldFailFast_WhenModelTimeoutExceeded() {
        ChatModel chatModel = mock(ChatModel.class);
        doAnswer(invocation -> {
            Thread.sleep(200);
            return "Too late";
        }).when(chatModel).call(anyString());

        ObjectProvider<ChatModel> provider = () -> chatModel;
        AiAssistantProperties properties = new AiAssistantProperties(
                true,
                1000,
                new AiAssistantProperties.Retry(1, 10),
                new AiAssistantProperties.Timeout(50),
                new AiAssistantProperties.RateLimit(true, 20, 60));

        FashionAssistantService service = new FashionAssistantService(provider, properties, new SimpleMeterRegistry());

        assertThatThrownBy(() -> service.chat("Tư vấn nhanh"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("AI chat failed after retries");
    }
}
