package com.skaly.fashion_backend.product.application;

import com.skaly.fashion_backend.product.application.event.ProductCreatedEvent;
import com.skaly.fashion_backend.product.application.event.ProductUpdatedEvent;
import com.skaly.fashion_backend.common.port.EmbeddingModelPort;
import com.skaly.fashion_backend.product.domain.port.ProductEmbeddingPort;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;
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
public class ProductEmbeddingService implements ProductEmbeddingPort {

    private static final int REINDEX_PAGE_SIZE = 5_000;

    private final ProductRepository productRepository;
    private final EmbeddingModelPort embeddingModelPort;

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
        productRepository.findAll(PageRequest.of(0, REINDEX_PAGE_SIZE))
                .getContent()
                .stream()
                .filter(p -> p.getEmbeddingVector() == null)
                .forEach(p -> generateEmbeddingForProductId(p.getId(), "manual-reindex"));
        log.info("Finished manual re-indexing");
    }

    private void generateEmbeddingForProductId(UUID productId, String type) {
        log.info("Generating embedding for {} ProductEntity ID: {}", type, productId);

        productRepository.findById(productId).ifPresent(product -> {
            try {
                // Prepare content for embedding
                String categoryName = product.getCategory() != null ? product.getCategory().getName() : "";
                String contentToEmbed = String.format("ProductEntity: %s. CategoryEntity: %s. Description: %s",
                        product.getName(),
                        categoryName,
                        product.getDescription() != null ? product.getDescription() : "");

                // Call Embedding Model via Port (AI Provider Agnostic)
                float[] embeddingArray = embeddingModelPort.embed(contentToEmbed);

                // Update product
                product.setEmbeddingVector(embeddingArray);
                productRepository.save(product);
                log.info("Successfully saved embedding for ProductEntity ID: {}", productId);
            } catch (Exception e) {
                log.error("Failed to generate embedding for ProductEntity ID: {}", productId, e);
            }
        });
    }

    @Override
    public float[] embedQuery(String query) {
        return embeddingModelPort.embed(query);
    }

    @Override
    public java.util.List<RelatedProduct> searchRelatedProducts(float[] queryVector, int limit) {
        return productRepository.findTopKByEmbeddingVectorClosestTo(queryVector, limit)
                .stream()
                .map(p -> new RelatedProduct(p.getName(), p.getBasePrice(), p.getDescription()))
                .collect(java.util.stream.Collectors.toList());
    }
}
