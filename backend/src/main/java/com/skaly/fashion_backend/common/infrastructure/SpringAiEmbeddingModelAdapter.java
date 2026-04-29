package com.skaly.fashion_backend.common.infrastructure;

import com.skaly.fashion_backend.common.port.EmbeddingModelPort;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

/**
 * Adapter implementation for Spring AI Embedding Model
 * Implements EmbeddingModelPort interface - Hexagonal Architecture
 */
@Component
public class SpringAiEmbeddingModelAdapter implements EmbeddingModelPort {

    private final EmbeddingModel embeddingModel;

    public SpringAiEmbeddingModelAdapter(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public float[] embed(String content) {
        return embeddingModel.embed(content);
    }

}