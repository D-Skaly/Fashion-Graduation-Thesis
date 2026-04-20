package com.skaly.fashion_backend.product.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    private String size; // S, M, L

    @Column(nullable = false)
    private String color; // Red, Blue

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "price_adjustment")
    private BigDecimal priceAdjustment;

    @Column(name = "sku_code", unique = true)
    private String skuCode;
}


