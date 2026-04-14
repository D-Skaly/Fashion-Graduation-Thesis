package com.skaly.fashion_backend.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(value = "SELECT * FROM products p WHERE p.embedding_vector IS NOT NULL ORDER BY p.embedding_vector <=> cast(:vector as vector) LIMIT :limit", nativeQuery = true)
    List<Product> findTopKByEmbeddingVectorClosestTo(@Param("vector") float[] vector, @Param("limit") int limit);

    // Search by keyword (name or description)
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
           "ORDER BY p.soldCount DESC")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Filter by category with optional price range
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:minPrice IS NULL OR p.basePrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.basePrice <= :maxPrice)" +
           "ORDER BY p.createdAt DESC")
    Page<Product> findByFilters(@Param("categoryId") UUID categoryId,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice,
                                Pageable pageable);

    // Get featured products
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isFeatured = true ORDER BY p.createdAt DESC")
    Page<Product> findFeaturedProducts(Pageable pageable);

    // Get new arrivals (recently created)
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    Page<Product> findNewArrivals(Pageable pageable);

    // Get products by brand
    Page<Product> findByBrandAndIsActiveTrue(String brand, Pageable pageable);

    // Get products by tags
    @Query("SELECT p FROM Product p JOIN p.tags t WHERE p.isActive = true AND t = :tag")
    Page<Product> findByTag(@Param("tag") String tag, Pageable pageable);

    // Get all active brands
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL AND p.isActive = true ORDER BY p.brand")
    List<String> findAllBrands();

    // Get all active tags
    @Query("SELECT DISTINCT t FROM Product p JOIN p.tags t WHERE t IS NOT NULL AND p.isActive = true")
    List<String> findAllTags();
}
