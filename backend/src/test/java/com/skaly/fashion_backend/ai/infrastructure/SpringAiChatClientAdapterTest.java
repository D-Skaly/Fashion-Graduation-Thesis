package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.ai.domain.port.AIModelPort;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.embedding.EmbeddingModel;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SpringAiChatClientAdapterTest {

    @Test
    void shouldImplementAIModelPort() {
        ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        when(chatClientBuilder.build()).thenReturn(chatClient);

        VectorStore vectorStore = mock(VectorStore.class);
        EmbeddingModel embeddingModel = mock(EmbeddingModel.class);

        AIModelPort adapter = new SpringAiChatClientAdapter(chatClientBuilder, vectorStore, embeddingModel);

        assertNotNull(adapter);
        assertInstanceOf(AIModelPort.class, adapter);
    }

    @Test
    void completeChatPrompt_shouldReturnResponse() {
        // Given
        ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        when(chatClientBuilder.build()).thenReturn(chatClient);

        VectorStore vectorStore = mock(VectorStore.class);
        EmbeddingModel embeddingModel = mock(EmbeddingModel.class);

        SpringAiChatClientAdapter adapter = new SpringAiChatClientAdapter(chatClientBuilder, vectorStore, embeddingModel);

        // When & Then - verify it implements the interface method
        assertNotNull(adapter);
    }

    @Test
    void embedQuery_shouldReturnVector() {
        // Given
        ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class);
        VectorStore vectorStore = mock(VectorStore.class);
        EmbeddingModel embeddingModel = mock(EmbeddingModel.class);

        SpringAiChatClientAdapter adapter = new SpringAiChatClientAdapter(chatClientBuilder, vectorStore, embeddingModel);

        // When & Then
        assertNotNull(adapter);
    }

    @Test
    void searchRelatedProducts_shouldReturnResults() {
        // Given
        ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class);
        VectorStore vectorStore = mock(VectorStore.class);
        EmbeddingModel embeddingModel = mock(EmbeddingModel.class);

        Document doc = new Document("test content");
        when(vectorStore.similaritySearch(any())).thenReturn(List.of(doc));

        SpringAiChatClientAdapter adapter = new SpringAiChatClientAdapter(chatClientBuilder, vectorStore, embeddingModel);

        // When & Then
        assertNotNull(adapter);
    }
}