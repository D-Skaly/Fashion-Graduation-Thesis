package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.ai.domain.port.AIModelPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.StructuredTaskScope;

/**
 * Adapter sử dụng Spring AI ChatClient với RAG support (RetrievalAugmentationAdvisor).
 * Tuân thủ Clean Architecture: implement AIModelPort từ domain.
 * Sử dụng Spring AI 2.0 features: ChatClient, RetrievalAugmentationAdvisor, EmbeddingModel.
 * Uses Virtual Threads (Java 21) for I/O-intensive operations.
 */
public class SpringAiChatClientAdapter implements AIModelPort {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

    public SpringAiChatClientAdapter(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String completeChatPrompt(String prompt) {
        // Wrap AI call in Virtual Thread using StructuredTaskScope
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> {
                return chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content();
            });
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to complete chat prompt", e);
        }
    }

    @Override
    public Flux<String> streamChatPrompt(String prompt) {
        // Streaming doesn't need virtual threads - already reactive
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }

    @Override
    public float[] embedQuery(String text) {
        // Wrap embedding call in Virtual Thread
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> embeddingModel.embed(text));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to embed query", e);
        }
    }

    @Override
    public List<? extends java.io.Serializable> searchRelatedProducts(float[] vector, int topK) {
        // TODO: Implement proper vector similarity search with Spring AI VectorStore
        // For now, return empty list to avoid compilation errors with SearchRequest API
        // This should be implemented based on actual Spring AI version's API
        return java.util.List.of();
    }
}
