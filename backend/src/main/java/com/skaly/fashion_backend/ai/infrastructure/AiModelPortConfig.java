package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.ai.domain.port.AIModelPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for AI Model Port adapter in ai module.
 * Provides SpringAiChatClientAdapter bean with proper dependencies.
 */
@Configuration
public class AiModelPortConfig {

    @Bean
    @ConditionalOnMissingBean(AIModelPort.class)
    @ConditionalOnProperty(prefix = "application.ai.assistant", name = "enabled", havingValue = "true")
    AIModelPort springAiChatClientAdapter(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore,
            EmbeddingModel embeddingModel) {
        return new SpringAiChatClientAdapter(chatClientBuilder, vectorStore, embeddingModel);
    }
}