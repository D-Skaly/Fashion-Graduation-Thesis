package com.skaly.fashion_backend.saga.application;

import com.skaly.fashion_backend.saga.application.SagaInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateInventoryStep implements SagaStep<OrderSagaContext> {

    private final SagaInventoryService sagaInventoryService;

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

            // Store original stock for compensation
            int currentStock = sagaInventoryService.getCurrentStock(variantId);
            originalStocks.put(variantId, currentStock);

            if (currentStock < quantity) {
                throw new RuntimeException("Insufficient stock for variant: " + variantId);
            }

            sagaInventoryService.reduceStock(variantId, quantity);

            log.info("Updated stock for variant {}: {} -> {}", variantId, currentStock,
                    sagaInventoryService.getCurrentStock(variantId));
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

                sagaInventoryService.restoreStock(variantId, originalStock);
                log.info("Restored stock for variant {}: {}", variantId, originalStock);
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
