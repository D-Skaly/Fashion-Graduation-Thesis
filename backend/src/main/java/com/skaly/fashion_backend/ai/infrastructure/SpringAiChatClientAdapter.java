package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.ai.domain.port.AIModelPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Adapter sử dụng Spring AI ChatClient với RAG support (RetrievalAugmentationAdvisor).
 * Tuân thủ Clean Architecture: implement AIModelPort từ domain.
 * Sử dụng Spring AI 2.0 features: ChatClient, RetrievalAugmentationAdvisor, EmbeddingModel.
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
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Override
    public Flux<String> streamChatPrompt(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }

    @Override
    public float[] embedQuery(String text) {
        return embeddingModel.embed(text);
    }

    @Override
    public List<? extends java.io.Serializable> searchRelatedProducts(float[] vector, int topK) {
        SearchRequest searchRequest = SearchRequest.query(vector).withTopK(topK);
        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        return documents;
    }
}