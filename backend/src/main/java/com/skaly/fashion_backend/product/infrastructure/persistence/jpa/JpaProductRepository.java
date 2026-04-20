package com.skaly.fashion_backend.product.infrastructure.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {

    @EntityGraph(value = "ProductEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    Optional<ProductEntity> findById(UUID id);

    @EntityGraph(value = "ProductEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query(value = "SELECT * FROM products p WHERE p.embedding_vector IS NOT NULL ORDER BY p.embedding_vector <=> cast(:vector as vector) LIMIT :limit", nativeQuery = true)
    List<ProductEntity> findTopKByEmbeddingVectorClosestTo(@Param("vector") float[] vector, @Param("limit") int limit);

    @EntityGraph(value = "ProductEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query(value = "SELECT * FROM products p WHERE p.is_active = true " +
           "AND (:categoryId IS NULL OR p.category_id = cast(:categoryId as uuid)) " +
           "AND (:minPrice IS NULL OR p.base_price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.base_price <= :maxPrice) " +
           "AND p.embedding_vector IS NOT NULL " +
           "ORDER BY p.embedding_vector <=> cast(:vector as vector) " +
           "LIMIT :limit", nativeQuery = true)
    List<ProductEntity> searchWithFilters(@Param("vector") float[] vector,
                                          @Param("categoryId") UUID categoryId,
                                          @Param("minPrice") BigDecimal minPrice,
                                          @Param("maxPrice") BigDecimal maxPrice,
                                          @Param("limit") int limit);

    @EntityGraph(value = "ProductEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query(value = "SELECT * FROM products p WHERE p.style_vector IS NOT NULL ORDER BY p.style_vector <=> cast(:vector as vector) LIMIT :limit", nativeQuery = true)
    List<ProductEntity> findTopKByStyleVectorClosestTo(@Param("vector") float[] vector, @Param("limit") int limit);

    // Search by keyword (name or description)
    @EntityGraph(value = "ProductEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
           "ORDER BY p.soldCount DESC")
    Page<ProductEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Filter by category with optional price range
    @EntityGraph(value = "ProductEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:minPrice IS NULL OR p.basePrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.basePrice <= :maxPrice)" +
           "ORDER BY p.createdAt DESC")
    Page<ProductEntity> findByFilters(@Param("categoryId") UUID categoryId,
                                      @Param("minPrice") BigDecimal minPrice,
                                      @Param("maxPrice") BigDecimal maxPrice,
                                      Pageable pageable);

    // Get featured products
    @EntityGraph(value = "ProductEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true AND p.isFeatured = true ORDER BY p.createdAt DESC")
    Page<ProductEntity> findFeaturedProducts(Pageable pageable);

    // Get new arrivals (recently created)
    @EntityGraph(value = "ProductEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    Page<ProductEntity> findNewArrivals(Pageable pageable);

    // Get products by brand
    @EntityGraph(value = "ProductEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    Page<ProductEntity> findByBrandAndIsActiveTrue(String brand, Pageable pageable);

    // Get products by tags
    @EntityGraph(value = "ProductEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT DISTINCT p FROM ProductEntity p JOIN p.tags t WHERE p.isActive = true AND t = :tag")
    Page<ProductEntity> findByTag(@Param("tag") String tag, Pageable pageable);

    @EntityGraph(value = "ProductEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    Page<ProductEntity> findAll(Pageable pageable);

    // Get all active brands
    @Query("SELECT DISTINCT p.brand FROM ProductEntity p WHERE p.brand IS NOT NULL AND p.isActive = true ORDER BY p.brand")
    List<String> findAllBrands();

    // Get all active tags
    @Query("SELECT DISTINCT t FROM ProductEntity p JOIN p.tags t WHERE t IS NOT NULL AND p.isActive = true")
    List<String> findAllTags();
}

