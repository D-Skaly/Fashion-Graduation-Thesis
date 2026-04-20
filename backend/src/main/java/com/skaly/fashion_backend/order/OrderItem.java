package com.skaly.fashion_backend.order;

import lombok.*;

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

