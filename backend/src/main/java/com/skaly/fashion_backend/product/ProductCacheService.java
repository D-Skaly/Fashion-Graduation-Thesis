package com.skaly.fashion_backend.product;

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

    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final String FEATURED_PRODUCTS_KEY = "products:featured";
    private static final String NEW_ARRIVALS_KEY = "products:new-arrivals";
    private static final String BRANDS_KEY = "products:brands";
    private static final String TAGS_KEY = "products:tags";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    public ProductResponse getProductById(UUID id) {
        String key = PRODUCT_KEY_PREFIX + id;
        ProductResponse cached = (ProductResponse) redisTemplate.opsForValue().get(key);
        
        if (cached != null) {
            log.debug("Cache hit for product: {}", id);
            return cached;
        }

        log.debug("Cache miss for product: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        ProductResponse response = productMapper.toProductResponse(product);
        
        redisTemplate.opsForValue().set(key, response, CACHE_TTL);
        return response;
    }

    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        // For paginated results, we cache individual products but not the page
        return productRepository.findFeaturedProducts(pageable)
                .map(productMapper::toProductResponse);
    }

    public Page<ProductResponse> getNewArrivals(Pageable pageable) {
        return productRepository.findNewArrivals(pageable)
                .map(productMapper::toProductResponse);
    }

    public List<String> getAllBrands() {
        List<String> cached = (List<String>) redisTemplate.opsForValue().get(BRANDS_KEY);
        
        if (cached != null) {
            log.debug("Cache hit for brands");
            return cached;
        }

        log.debug("Cache miss for brands");
        List<String> brands = productRepository.findAllBrands();
        redisTemplate.opsForValue().set(BRANDS_KEY, brands, CACHE_TTL);
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
        redisTemplate.opsForValue().set(TAGS_KEY, tags, CACHE_TTL);
        return tags;
    }

    public void evictProduct(UUID id) {
        String key = PRODUCT_KEY_PREFIX + id;
        redisTemplate.delete(key);
        log.debug("Evicted cache for product: {}", id);
    }

    public void evictAllProducts() {
        // Evict all product keys
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        log.debug("Evicted all product cache");
    }

    public void evictBrands() {
        redisTemplate.delete(BRANDS_KEY);
        log.debug("Evicted brands cache");
    }

    public void evictTags() {
        redisTemplate.delete(TAGS_KEY);
        log.debug("Evicted tags cache");
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
