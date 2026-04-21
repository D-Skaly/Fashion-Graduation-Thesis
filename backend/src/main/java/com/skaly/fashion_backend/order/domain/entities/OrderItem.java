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
@Builder
public class OrderItem {
    private UUID id;
    private UUID productVariantId;
    private Integer quantity;
    private BigDecimal snapshotPrice;
}
