package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.order.OrderStatus;
import lombok.*;

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
        items.add(item);
    }

    public void cancel(String reason) {
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

