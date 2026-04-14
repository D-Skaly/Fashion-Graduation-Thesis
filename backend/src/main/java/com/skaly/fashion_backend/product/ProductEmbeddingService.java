package com.skaly.fashion_backend.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEmbeddingService {

    private final ProductRepository productRepository;
    private final EmbeddingModel embeddingModel;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        generateEmbeddingForProductId(event.getProductId(), "new");
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductUpdatedEvent(ProductUpdatedEvent event) {
        generateEmbeddingForProductId(event.getProductId(), "updated");
    }

    @Transactional
    public void generateEmbeddingsForAllMissing() {
        log.info("Starting manual re-indexing for products with missing embeddings");
        productRepository.findAll().stream()
                .filter(p -> p.getEmbeddingVector() == null)
                .forEach(p -> generateEmbeddingForProductId(p.getId(), "manual-reindex"));
        log.info("Finished manual re-indexing");
    }

    private void generateEmbeddingForProductId(UUID productId, String type) {
        log.info("Generating embedding for {} Product ID: {}", type, productId);

        productRepository.findById(productId).ifPresent(product -> {
            try {
                // Prepare content for embedding
                String categoryName = product.getCategory() != null ? product.getCategory().getName() : "";
                String contentToEmbed = String.format("Product: %s. Category: %s. Description: %s",
                        product.getName(),
                        categoryName,
                        product.getDescription() != null ? product.getDescription() : "");

                // Call OpenAI / Embedding Model
                float[] embeddingArray = embeddingModel.embed(contentToEmbed);

                // Update product
                product.setEmbeddingVector(embeddingArray);
                productRepository.save(product);
                log.info("Successfully saved embedding for Product ID: {}", productId);
            } catch (Exception e) {
                log.error("Failed to generate embedding for Product ID: {}", productId, e);
            }
        });
    }

    public float[] embedQuery(String query) {
        return embeddingModel.embed(query);
    }
}
