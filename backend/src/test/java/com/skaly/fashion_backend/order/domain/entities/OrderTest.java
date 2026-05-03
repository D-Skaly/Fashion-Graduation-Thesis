package com.skaly.fashion_backend.order.domain.entities;

import com.skaly.fashion_backend.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private UUID variantId1;
    private UUID variantId2;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .shippingAddress("123 Main St")
                .build();

        variantId1 = UUID.randomUUID();
        variantId2 = UUID.randomUUID();
    }

    @Test
    void addItem_shouldAddItemToOrder() {
        OrderItem item = OrderItem.builder()
                .productVariantId(variantId1)
                .quantity(2)
                .snapshotPrice(new BigDecimal("50.00"))
                .build();

        order.addItem(item);

        assertEquals(1, order.getItems().size());
        assertTrue(order.getItems().contains(item));
    }

    @Test
    void addItem_shouldThrowExceptionWhenNotPending() {
        order.confirm(); // Changes status to CONFIRMED

        OrderItem item = OrderItem.builder()
                .productVariantId(variantId1)
                .quantity(1)
                .snapshotPrice(BigDecimal.TEN)
                .build();

        assertThrows(com.skaly.fashion_backend.order.domain.InvalidOrderStateException.class, () -> {
            order.addItem(item);
        });
    }

    @Test
    void confirm_shouldChangeStatusToConfirmed() {
        OrderItem item = OrderItem.builder()
                .productVariantId(variantId1)
                .quantity(1)
                .snapshotPrice(new BigDecimal("100.00"))
                .build();
        order.addItem(item);

        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void confirm_shouldThrowExceptionWhenNoItems() {
        assertThrows(IllegalStateException.class, () -> {
            order.confirm();
        });
    }

    @Test
    void confirm_shouldThrowExceptionWhenNotPending() {
        order.confirm(); // Now it's CONFIRMED

        OrderItem item = OrderItem.builder()
                .productVariantId(variantId1)
                .quantity(1)
                .snapshotPrice(BigDecimal.TEN)
                .build();

        assertThrows(com.skaly.fashion_backend.order.domain.InvalidOrderStateException.class, () -> {
            order.confirm();
        });
    }

    @Test
    void ship_shouldChangeStatusToShipped() {
        OrderItem item = OrderItem.builder()
                .productVariantId(variantId1)
                .quantity(1)
                .snapshotPrice(new BigDecimal("100.00"))
                .build();
        order.addItem(item);
        order.confirm();

        order.ship();

        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void ship_shouldThrowExceptionWhenNotConfirmed() {
        assertThrows(com.skaly.fashion_backend.order.domain.InvalidOrderStateException.class, () -> {
            order.ship();
        });
    }

    @Test
    void complete_shouldChangeStatusToCompleted() {
        OrderItem item = OrderItem.builder()
                .productVariantId(variantId1)
                .quantity(1)
                .snapshotPrice(new BigDecimal("100.00"))
                .build();
        order.addItem(item);
        order.confirm();
        order.ship();

        order.complete();

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void cancel_shouldChangeStatusToCancelled() {
        order.cancel("Customer request");

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertNotNull(order.getCancelledAt());
        assertEquals("Customer request", order.getCancelledReason());
    }

    @Test
    void cancel_shouldThrowExceptionWhenShipped() {
        OrderItem item = OrderItem.builder()
                .productVariantId(variantId1)
                .quantity(1)
                .snapshotPrice(new BigDecimal("100.00"))
                .build();
        order.addItem(item);
        order.confirm();
        order.ship();

        assertThrows(com.skaly.fashion_backend.order.domain.InvalidOrderStateException.class, () -> {
            order.cancel("Too late");
        });
    }

    @Test
    void calculateTotal_shouldCalculateCorrectTotal() {
        OrderItem item1 = OrderItem.builder()
                .productVariantId(variantId1)
                .quantity(2)
                .snapshotPrice(new BigDecimal("50.00"))
                .build();

        OrderItem item2 = OrderItem.builder()
                .productVariantId(variantId2)
                .quantity(1)
                .snapshotPrice(new BigDecimal("100.00"))
                .build();

        order.addItem(item1);
        order.addItem(item2);

        // Total should be (50 * 2) + (100 * 1) = 200
        assertEquals(new BigDecimal("200.00"), order.getSubTotal());
    }
}