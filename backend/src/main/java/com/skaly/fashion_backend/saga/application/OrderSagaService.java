package com.skaly.fashion_backend.saga.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSagaService {

    private final SagaOrchestrator<OrderSagaContext> sagaOrchestrator;
    private final CreateOrderStep createOrderStep;
    private final ProcessPaymentStep processPaymentStep;
    private final UpdateInventoryStep updateInventoryStep;

    @Transactional
    public boolean executeOrderSaga(UUID orderId, UUID userId, String orderNumber, 
                                      Map<UUID, Integer> productVariantsWithQuantity) {
        log.info("Starting order saga for order: {}", orderId);
        
        OrderSagaContext context = new OrderSagaContext();
        context.setOrderId(orderId);
        context.setUserId(userId);
        context.setOrderNumber(orderNumber);
        context.setProductVariantsWithQuantity(productVariantsWithQuantity);
        context.setOriginalStockQuantities(new HashMap<>());
        
        // Build saga
        sagaOrchestrator.reset();
        sagaOrchestrator
                .addStep(createOrderStep)
                .addStep(processPaymentStep)
                .addStep(updateInventoryStep);
        
        // Execute saga
        boolean success = sagaOrchestrator.execute(context);
        
        if (!success) {
            context.setErrorMessage("OrderEntity processing failed");
            context.setCompensationRequired(true);
            log.error("OrderEntity saga failed for order: {}", orderId);
        } else {
            log.info("OrderEntity saga completed successfully for order: {}", orderId);
        }
        
        return success;
    }

    @Transactional
    public void compensateOrderSaga(UUID orderId, UUID userId, Map<UUID, Integer> originalStockQuantities) {
        log.info("Starting compensation for order: {}", orderId);
        
        OrderSagaContext context = new OrderSagaContext();
        context.setOrderId(orderId);
        context.setUserId(userId);
        context.setOriginalStockQuantities(originalStockQuantities);
        context.setCompensationRequired(true);
        
        // Build saga for compensation
        sagaOrchestrator.reset();
        sagaOrchestrator
                .addStep(createOrderStep)
                .addStep(processPaymentStep)
                .addStep(updateInventoryStep);
        
        // Compensate
        sagaOrchestrator.compensate(context);
        
        log.info("OrderEntity saga compensation completed for order: {}", orderId);
    }
}
