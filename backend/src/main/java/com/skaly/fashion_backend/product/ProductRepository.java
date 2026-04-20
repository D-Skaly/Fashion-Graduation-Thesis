package com.skaly.fashion_backend.product;

import com.skaly.fashion_backend.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    Page<Product> findAll(Pageable pageable);
    
    List<Product> findTopKByEmbeddingVectorClosestTo(float[] vector, int limit);
    List<Product> findTopKByStyleVectorClosestTo(float[] vector, int limit);
    
    Page<Product> searchByKeyword(String keyword, Pageable pageable);
    Page<Product> findByFilters(UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    Page<Product> findFeaturedProducts(Pageable pageable);
    Page<Product> findNewArrivals(Pageable pageable);
    
    Page<Product> findByBrandAndIsActiveTrue(String brand, Pageable pageable);
    Page<Product> findByTag(String tag, Pageable pageable);
    
    List<String> findAllBrands();
    List<String> findAllTags();
}

