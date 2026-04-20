package com.skaly.fashion_backend.cart;
import com.skaly.fashion_backend.cart.CartEntity;

import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductVariantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cart_items", indexes = {
    @Index(name = "idx_cart_item_cart_id", columnList = "cart_id"),
    @Index(name = "idx_cart_item_product_variant_id", columnList = "product_variant_id"),
    @Index(name = "idx_cart_item_added_at", columnList = "addedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariantEntity productVariant;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "snapshot_price", nullable = false)
    private BigDecimal snapshotPrice;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Transient
    @Builder.Default
    private boolean quantityAdjusted = false;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
}


