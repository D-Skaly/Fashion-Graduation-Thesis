package com.skaly.fashion_backend.order.domain.entities;

import com.skaly.fashion_backend.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private OrderItem validItem;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .build();

        validItem = OrderItem.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .productName("Test Product")
                .snapshotPrice(new BigDecimal("100.00"))
                .quantity(2)
                .build();
    }

    @Test
    void addItem_shouldAddItemAndRecalculateTotal() {
        order.addItem(validItem);

        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("200.00"), order.getTotalAmount());
    }

    @Test
    void addItem_shouldThrowExceptionWhenOrderNotPending() {
        order.confirm();

        assertThrows(com.skaly.fashion_backend.order.domain.InvalidOrderStateException.class,
                () -> order.addItem(validItem));
    }

    @Test
    void addItem_shouldThrowExceptionWhenOrderCanceled() {
        order.cancel("Test reason");

        assertThrows(com.skaly.fashion_backend.order.domain.InvalidOrderStateException.class,
                () -> order.addItem(validItem));
    }

    @Test
    void confirm_shouldChangeStatusToConfirmed() {
        order.addItem(validItem);
        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void confirm_shouldThrowExceptionWhenNoItems() {
        assertThrows(IllegalStateException.class, () -> order.confirm());
    }

    @Test
    void confirm_shouldThrowExceptionWhenNotPending() {
        order.confirm();

        assertThrows(com.skaly.fashion_backend.order.domain.InvalidOrderStateException.class,
                () -> order.confirm());
    }

    @Test
    void markAsPaid_shouldChangeStatusToConfirmed() {
        order.markAsPaid();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void ship_shouldChangeStatusToShipped() {
        order.addItem(validItem);
        order.confirm();
        order.ship();

        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void ship_shouldThrowExceptionWhenNotConfirmed() {
        assertThrows(com.skaly.fashion_backend.order.domain.InvalidOrderStateException.class,
                () -> order.ship());
    }

    @Test
    void complete_shouldChangeStatusToCompleted() {
        order.addItem(validItem);
        order.confirm();
        order.ship();
        order.complete();

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void complete_shouldThrowExceptionWhenNotShipped() {
        assertThrows(com.skaly.fashion_backend.order.domain.InvalidOrderStateException.class,
                () -> order.complete());
    }

    @Test
    void cancel_shouldChangeStatusToCancelled() {
        order.cancel("Customer request");

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals("Customer request", order.getCancelledReason());
        assertNotNull(order.getCancelledAt());
    }

    @Test
    void cancel_shouldThrowExceptionWhenShipped() {
        order.addItem(validItem);
        order.confirm();
        order.ship();

        assertThrows(com.skaly.fashion_backend.order.domain.InvalidOrderStateException.class,
                () -> order.cancel("Test reason"));
    }

    @Test
    void cancel_shouldThrowExceptionWhenCompleted() {
        order.addItem(validItem);
        order.confirm();
        order.ship();
        order.complete();

        assertThrows(com.skaly.fashion_backend.order.domain.InvalidOrderStateException.class,
                () -> order.cancel("Test reason"));
    }

    @Test
    void calculateTotal_shouldIncludeSubtotalTaxShippingAndDiscount() {
        OrderItem item1 = OrderItem.builder()
                .snapshotPrice(new BigDecimal("50.00"))
                .quantity(2)
                .build();
        OrderItem item2 = OrderItem.builder()
                .snapshotPrice(new BigDecimal("30.00"))
                .quantity(1)
                .build();

        order.addItem(item1);
        order.addItem(item2);

        order.setTaxAmount(new BigDecimal("10.00"));
        order.setShippingCost(new BigDecimal("5.00"));
        order.setDiscountAmount(new BigDecimal("15.00"));

        order.calculateTotal();

        // Subtotal: 50*2 + 30*1 = 130
        // Total: 130 + 10 + 5 - 15 = 130
        assertEquals(new BigDecimal("130.00"), order.getSubTotal());
        assertEquals(new BigDecimal("130.00"), order.getTotalAmount());
    }
}