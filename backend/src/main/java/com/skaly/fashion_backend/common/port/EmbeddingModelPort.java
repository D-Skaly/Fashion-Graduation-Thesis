package com.skaly.fashion_backend.common.port;

/**
 * Port interface for embedding model - AI Provider Agnostic
 * Follows Hexagonal Architecture (Ports & Adapters) pattern
 */
public interface EmbeddingModelPort {

    /**
     * Generates vector embedding for given text content
     * 
     * @param content Text content to embed
     * @return float array embedding vector
     */
    float[] embed(String content);

}