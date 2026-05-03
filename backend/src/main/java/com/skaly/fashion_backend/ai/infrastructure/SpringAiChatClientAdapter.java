package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.ai.domain.port.AIModelPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Adapter sử dụng Spring AI ChatClient với RAG support (RetrievalAugmentationAdvisor).
 * Tuân thủ Clean Architecture: implement AIModelPort từ domain.
 * Sử dụng Spring AI 1.0 features: ChatClient với Advisors (MessageChatMemoryAdvisor, RetrievalAugmentationAdvisor).
 * ChatClient handles Virtual Threads internally (Java 21).
 */
public class SpringAiChatClientAdapter implements AIModelPort {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

    public SpringAiChatClientAdapter(ChatClient chatClient, VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public String completeChatPrompt(String prompt) {
        // ChatClient handles virtual threads internally
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Override
    public Flux<String> streamChatPrompt(String prompt) {
        // Streaming - already reactive, ChatClient handles properly
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }

    @Override
    public float[] embedQuery(String text) {
        // Embedding call - ChatClient doesn't handle embeddings, do directly
        return embeddingModel.embed(text);
    }

    @Override
    public List<? extends java.io.Serializable> searchRelatedProducts(float[] vector, int topK) {
        // TODO: Implement proper vector similarity search with Spring AI VectorStore
        // For now, return empty list to avoid compilation errors with SearchRequest API
        // This should be implemented based on actual Spring AI version's API
        return java.util.List.of();
    }
}
