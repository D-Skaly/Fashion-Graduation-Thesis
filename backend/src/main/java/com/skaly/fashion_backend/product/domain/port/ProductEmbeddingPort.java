package com.skaly.fashion_backend.product.domain.port;

/**
 * Port for embedding operations on products.
 * Allows other modules (like ai) to access embedding functionality
 * without depending on product module's application layer.
 */
public interface ProductEmbeddingPort {

    /**
     * Generate embedding vector for a text query.
     * Used for RAG (Retrieval-Augmented Generation) to find similar products.
     *
     * @param query The text query to embed
     * @return float array representing the embedding vector
     */
    float[] embedQuery(String query);

    /**
     * Trigger manual re-indexing for all products with missing embeddings.
     * This is an administrative operation typically called from admin endpoints.
     */
    void generateEmbeddingsForAllMissing();

    /**
     * Search for products related to a query vector.
     * Returns a list of simple product snapshots for AI context.
     */
    java.util.List<RelatedProduct> searchRelatedProducts(float[] queryVector, int limit);

    record RelatedProduct(String name, java.math.BigDecimal price, String description) {}
}
