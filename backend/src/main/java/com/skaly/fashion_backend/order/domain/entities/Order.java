package com.skaly.fashion_backend.order.domain.entities;

import com.skaly.fashion_backend.order.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private UUID id;
    private String orderNumber;
    private UUID userId;

    @Builder.Default
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    private String discountCode;

    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private String shippingAddress;
    private String notes;
    private LocalDateTime cancelledAt;
    private String cancelledReason;

    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void addItem(OrderItem item) {
        if (this.status != OrderStatus.PENDING) {
            throw new com.skaly.fashion_backend.order.domain.InvalidOrderStateException("Cannot add items to " + this.status + " order");
        }
        items.add(item);
        calculateTotal();
    }

    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new com.skaly.fashion_backend.order.domain.InvalidOrderStateException("Only PENDING orders can be confirmed");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm order with no items");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void markAsPaid() {
        if (this.status != OrderStatus.PENDING) {
            throw new com.skaly.fashion_backend.order.domain.InvalidOrderStateException("Only PENDING orders can be marked as paid");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void ship() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new com.skaly.fashion_backend.order.domain.InvalidOrderStateException("Only CONFIRMED orders can be shipped");
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void complete() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new com.skaly.fashion_backend.order.domain.InvalidOrderStateException("Only SHIPPED orders can be completed");
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel(String reason) {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.COMPLETED) {
            throw new com.skaly.fashion_backend.order.domain.InvalidOrderStateException("Cannot cancel " + this.status + " order");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledReason = reason;
    }

    public void calculateTotal() {
        this.subTotal = items.stream()
                .map(item -> item.getSnapshotPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalAmount = subTotal
                .add(taxAmount)
                .add(shippingCost)
                .subtract(discountAmount);
    }
}
