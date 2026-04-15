package com.skaly.fashion_backend.saga;

import com.skaly.fashion_backend.product.ProductVariant;
import com.skaly.fashion_backend.product.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateInventoryStep implements SagaStep<OrderSagaContext> {

    private final ProductVariantRepository productVariantRepository;

    @Override
    public String getName() {
        return "UpdateInventory";
    }

    @Override
    public void execute(OrderSagaContext context) {
        log.info("Updating inventory for order: {}", context.getOrderId());
        
        Map<UUID, Integer> variantsWithQuantity = context.getProductVariantsWithQuantity();
        Map<UUID, Integer> originalStocks = context.getOriginalStockQuantities();
        
        for (Map.Entry<UUID, Integer> entry : variantsWithQuantity.entrySet()) {
            UUID variantId = entry.getKey();
            Integer quantity = entry.getValue();
            
            ProductVariant variant = productVariantRepository.findById(variantId)
                    .orElseThrow(() -> new RuntimeException("Product variant not found: " + variantId));
            
            // Store original stock for compensation
            originalStocks.put(variantId, variant.getStockQuantity());
            
            // Reduce stock
            if (variant.getStockQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock for variant: " + variantId);
            }
            
            variant.setStockQuantity(variant.getStockQuantity() - quantity);
            productVariantRepository.save(variant);
            
            log.info("Updated stock for variant {}: {} -> {}", variantId, originalStocks.get(variantId), variant.getStockQuantity());
        }
        
        context.setOriginalStockQuantities(originalStocks);
    }

    @Override
    public void compensate(OrderSagaContext context) {
        log.info("Compensating UpdateInventory: restoring stock for order {}", context.getOrderId());
        
        try {
            Map<UUID, Integer> originalStocks = context.getOriginalStockQuantities();
            
            for (Map.Entry<UUID, Integer> entry : originalStocks.entrySet()) {
                UUID variantId = entry.getKey();
                Integer originalStock = entry.getValue();
                
                ProductVariant variant = productVariantRepository.findById(variantId)
                        .orElse(null);
                
                if (variant != null) {
                    variant.setStockQuantity(originalStock);
                    productVariantRepository.save(variant);
                    log.info("Restored stock for variant {}: {}", variantId, originalStock);
                }
            }
        } catch (Exception e) {
            log.error("Failed to compensate UpdateInventory", e);
        }
    }

    @Override
    public boolean canCompensate(OrderSagaContext context) {
        return context.getOriginalStockQuantities() != null && !context.getOriginalStockQuantities().isEmpty();
    }
}
