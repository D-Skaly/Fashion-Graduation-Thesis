package com.skaly.fashion_backend.order.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "orderItemBuilder")
public class OrderItem {
    private UUID id;
    private UUID productVariantId;
    private Integer quantity;
    private BigDecimal snapshotPrice;

    // Custom builder to auto-generate ID
    public static OrderItemBuilder builder() {
        return orderItemBuilder()
                .id(UUID.randomUUID());
    }

    public static class OrderItemBuilder {
        private UUID id = UUID.randomUUID();

        public OrderItemBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(id, productVariantId, quantity, snapshotPrice);
        }
    }
}
