package com.skaly.fashion_backend.product.application;

import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.product.interfaces.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Cache key structure
    private static final String CACHE_PREFIX = "fashion:";
    private static final String PRODUCT_DETAIL_PREFIX = CACHE_PREFIX + "product:detail:";
    private static final String PRODUCT_LIST_PREFIX = CACHE_PREFIX + "product:list:";
    private static final String PRODUCT_METADATA_PREFIX = CACHE_PREFIX + "product:metadata:";
    private static final String PRODUCT_VIEWS_PREFIX = CACHE_PREFIX + "product:views:";

    private static final String FEATURED_PRODUCTS_KEY = PRODUCT_LIST_PREFIX + "featured";
    private static final String NEW_ARRIVALS_KEY = PRODUCT_LIST_PREFIX + "new-arrivals";
    private static final String BRANDS_KEY = PRODUCT_METADATA_PREFIX + "brands";
    private static final String TAGS_KEY = PRODUCT_METADATA_PREFIX + "tags";

    // TTL Strategy
    private static final Duration DETAIL_TTL = Duration.ofHours(24);
    private static final Duration LIST_TTL = Duration.ofHours(1);
    private static final Duration METADATA_TTL = Duration.ofHours(12);

    public ProductResponse getProductById(UUID id) {
        String key = PRODUCT_DETAIL_PREFIX + id;
        ProductResponse cached = (ProductResponse) redisTemplate.opsForValue().get(key);
        
        if (cached != null) {
            log.debug("Cache hit for product: {}", id);
            return cached;
        }

        log.debug("Cache miss for product: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        ProductResponse response = productMapper.toProductResponseFromDomain(product);
        
        redisTemplate.opsForValue().set(key, response, DETAIL_TTL);
        return response;
    }

    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        // NOTE: We don't cache Page objects directly here yet as requested in original code comment,
        // but we'll maintain the structure. If production-level, we might want to cache the first page.
        return productRepository.findFeaturedProducts(pageable)
                .map(productMapper::toProductResponseFromDomain);
    }

    public Page<ProductResponse> getNewArrivals(Pageable pageable) {
        return productRepository.findNewArrivals(pageable)
                .map(productMapper::toProductResponseFromDomain);
    }

    public List<String> getAllBrands() {
        List<String> cached = (List<String>) redisTemplate.opsForValue().get(BRANDS_KEY);
        
        if (cached != null) {
            log.debug("Cache hit for brands");
            return cached;
        }

        log.debug("Cache miss for brands");
        List<String> brands = productRepository.findAllBrands();
        redisTemplate.opsForValue().set(BRANDS_KEY, brands, METADATA_TTL);
        return brands;
    }

    public List<String> getAllTags() {
        List<String> cached = (List<String>) redisTemplate.opsForValue().get(TAGS_KEY);
        
        if (cached != null) {
            log.debug("Cache hit for tags");
            return cached;
        }

        log.debug("Cache miss for tags");
        List<String> tags = productRepository.findAllTags();
        redisTemplate.opsForValue().set(TAGS_KEY, tags, METADATA_TTL);
        return tags;
    }

    public void evictProduct(UUID id) {
        String key = PRODUCT_DETAIL_PREFIX + id;
        redisTemplate.delete(key);
        // Also evict lists that might contain this product to ensure consistency
        evictProductLists();
        log.debug("Evicted cache for product: {}", id);
    }

    public void evictAllProducts() {
        // Evict only product-related keys using pattern to avoid flushDb()
        String pattern = CACHE_PREFIX + "product:*";
        java.util.Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        log.debug("Evicted all product cache using pattern: {}", pattern);
    }

    public void evictProductLists() {
        String pattern = PRODUCT_LIST_PREFIX + "*";
        java.util.Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        log.debug("Evicted all product list cache");
    }

    public void evictBrands() {
        redisTemplate.delete(BRANDS_KEY);
        log.debug("Evicted brands cache");
    }

    public void evictTags() {
        redisTemplate.delete(TAGS_KEY);
        log.debug("Evicted tags cache");
    }

    public void incrementViewCount(UUID productId) {
        String key = PRODUCT_VIEWS_PREFIX + productId;
        redisTemplate.opsForValue().increment(key);
        log.debug("Incremented view count in Redis for product: {}", productId);
    }

    public java.util.Map<UUID, Long> getViewCountsAndClear() {
        String pattern = PRODUCT_VIEWS_PREFIX + "*";
        java.util.Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return java.util.Collections.emptyMap();
        }

        java.util.Map<UUID, Long> viewCounts = new java.util.HashMap<>();
        for (String key : keys) {
            Object val = redisTemplate.opsForValue().get(key);
            if (val != null) {
                try {
                    UUID productId = UUID.fromString(key.substring(PRODUCT_VIEWS_PREFIX.length()));
                    long count = Long.parseLong(val.toString());
                    viewCounts.put(productId, count);
                } catch (Exception e) {
                    log.error("Error parsing view count for key: {}", key, e);
                }
            }
        }

        // Clear the keys in Redis after collecting them
        redisTemplate.delete(keys);
        log.debug("Collected and cleared {} view count keys from Redis", keys.size());
        return viewCounts;
    }

    public void warmUpCache(List<UUID> productIds) {
        productIds.parallelStream().forEach(id -> {
            try {
                getProductById(id);
            } catch (Exception e) {
                log.warn("Failed to warm up cache for product: {}", id, e);
            }
        });
        log.info("Warmed up cache for {} products", productIds.size());
    }
}

