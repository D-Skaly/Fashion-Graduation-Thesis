package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.ai.domain.port.AIModelPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for AI Model Port adapter in ai module.
 * Provides SpringAiChatClientAdapter bean with proper dependencies.
 * Configures ChatClient with Advisors (MessageChatMemoryAdvisor, RetrievalAugmentationAdvisor)
 * as required by docs/ai-integration.md.
 */
@Configuration
public class AiModelPortConfig {

    @Bean
    @ConditionalOnMissingBean(AIModelPort.class)
    @ConditionalOnProperty(prefix = "application.ai.assistant", name = "enabled", havingValue = "true")
    AIModelPort springAiChatClientAdapter(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore,
            EmbeddingModel embeddingModel,
            MessageChatMemoryAdvisor messageChatMemoryAdvisor) {
        
        // Build ChatClient with required Advisors for RAG pattern
        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(
                        messageChatMemoryAdvisor,
                        RetrievalAugmentationAdvisor.builder()
                                .documentRetriever(query -> vectorStore.similaritySearch(query.text()))
                                .build()
                )
                .build();
        
        return new SpringAiChatClientAdapter(chatClient, vectorStore, embeddingModel);
    }
}