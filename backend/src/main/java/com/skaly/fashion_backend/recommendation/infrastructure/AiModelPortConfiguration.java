package com.skaly.fashion_backend.recommendation.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skaly.fashion_backend.recommendation.domain.port.AIModelPort;
import com.skaly.fashion_backend.recommendation.infrastructure.gemini.GeminiAIAdapter;
import com.skaly.fashion_backend.recommendation.infrastructure.gemini.GeminiRestProperties;
import com.skaly.fashion_backend.recommendation.infrastructure.springai.SpringAiAIModelAdapter;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Chọn implementation {@link AIModelPort}: REST Gemini native (ưu tiên cấu hình) → Spring AI → no-op.
 */
@Configuration
@EnableConfigurationProperties(GeminiRestProperties.class)
public class AiModelPortConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "application.gemini.rest", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean(AIModelPort.class)
    AIModelPort geminiRestAdapter(GeminiRestProperties properties, ObjectMapper objectMapper) {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(properties.readTimeout());
        RestClient restClient = RestClient.builder().requestFactory(factory).build();
        return new GeminiAIAdapter(properties, restClient, objectMapper);
    }

    @Bean
    @ConditionalOnBean(ChatModel.class)
    @ConditionalOnMissingBean(AIModelPort.class)
    AIModelPort springAiAdapter(ChatModel chatModel, ObjectMapper objectMapper) {
        return new SpringAiAIModelAdapter(chatModel, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(AIModelPort.class)
    AIModelPort noopAiModelPort() {
        return new NoOpAIModelAdapter();
    }
}
