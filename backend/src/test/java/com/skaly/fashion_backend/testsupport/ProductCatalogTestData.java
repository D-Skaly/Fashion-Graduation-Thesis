package com.skaly.fashion_backend.testsupport;

import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.domain.model.ProductVariant;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Dữ liệu seed cho integration test qua {@link ProductRepository} (adapter + mapper JPA),
 * không tạo {@code ProductEntity} trực tiếp trong test.
 */
public final class ProductCatalogTestData {

    private ProductCatalogTestData() {
    }

    public static Product saveProductWithSingleVariant(
            ProductRepository productRepository,
            String nameStem,
            BigDecimal basePrice,
            String size,
            String color,
            int stockQuantity) {
        UUID u = UUID.randomUUID();
        ProductVariant variant = ProductVariant.builder()
                .sku("V-" + u)
                .size(size)
                .color(color)
                .stockQuantity(stockQuantity)
                .price(basePrice)
                .build();
        Product product = Product.builder()
                .name(nameStem)
                .sku("P-SKU-" + u)
                .slug("p-slug-" + u)
                .description("integration-test")
                .basePrice(basePrice)
                .variants(Set.of(variant))
                .build();
        return productRepository.save(product);
    }

    public static Product saveProductWithTwoVariants(
            ProductRepository productRepository,
            String nameStem,
            BigDecimal basePrice,
            int inStockQty,
            int outOfStockQty) {
        UUID u = UUID.randomUUID();
        ProductVariant inStock = ProductVariant.builder()
                .sku("V-A-" + u)
                .size("M")
                .color("Red")
                .stockQuantity(inStockQty)
                .price(basePrice)
                .build();
        ProductVariant out = ProductVariant.builder()
                .sku("V-B-" + u)
                .size("S")
                .color("Blue")
                .stockQuantity(outOfStockQty)
                .price(basePrice)
                .build();
        Product product = Product.builder()
                .name(nameStem)
                .sku("P2-SKU-" + u)
                .slug("p2-slug-" + u)
                .description("integration-test")
                .basePrice(basePrice)
                .variants(Set.of(inStock, out))
                .build();
        return productRepository.save(product);
    }

    public static UUID firstVariantIdMatchingStock(Product product, int stockQuantity) {
        return product.getVariants().stream()
                .filter(v -> v.getStockQuantity() != null && v.getStockQuantity() == stockQuantity)
                .map(ProductVariant::getId)
                .findFirst()
                .orElseThrow();
    }
}
