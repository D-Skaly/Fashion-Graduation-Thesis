package com.skaly.fashion_backend.product.application.event;

import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.JpaProductVariantRepository;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductVariantEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductVariantEventHandler {

    private final JpaProductVariantRepository productVariantRepository;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @Transactional(readOnly = true)
    public void handleProductVariantRequested(ProductVariantRequestedEvent event) {
        log.debug("Handling product variant request for cart: {}, variant: {}",
                event.cartId(), event.productVariantId());

        productVariantRepository.findById(event.productVariantId())
                .ifPresentOrElse(
                        variant -> publishResponse(event.cartId(), variant),
                        () -> publishErrorResponse(event.cartId(), event.productVariantId())
                );
    }

    private void publishResponse(UUID cartId, ProductVariantEntity variant) {
        var response = new ProductVariantResponseEvent(
                cartId,
                variant.getId(),
                variant.getProduct().getName(),
                variant.getSize(),
                variant.getColor(),
                variant.getProduct().getBasePrice(),
                variant.getPriceAdjustment(),
                variant.getStockQuantity()
        );
        eventPublisher.publishEvent(response);
    }

    private void publishErrorResponse(UUID cartId, UUID variantId) {
        var response = new ProductVariantResponseEvent(
                cartId,
                variantId,
                null, null, null, null, null, null
        );
        eventPublisher.publishEvent(response);
    }
}
