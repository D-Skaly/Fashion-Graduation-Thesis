package com.skaly.fashion_backend.product;

import com.skaly.fashion_backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search/semantic")
    public ResponseEntity<ApiResponse<java.util.List<ProductResponse>>> searchSemantic(
            @RequestParam("q") String query,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        java.util.List<ProductResponse> products = productService.searchProductsSemantically(query, limit);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // Search by keyword
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @RequestParam String keyword,
            @PageableDefault(size = 12) Pageable pageable) {
        Page<ProductResponse> products = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // Filter products with pagination
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @RequestParam(required = false) UUID category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sort,
            @PageableDefault(size = 12) Pageable pageable) {
        Page<ProductResponse> products = productService.filterProducts(category, minPrice, maxPrice, sortBy, sort, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // Get featured products
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getFeaturedProducts(
            @PageableDefault(size = 8) Pageable pageable) {
        Page<ProductResponse> products = productService.getFeaturedProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // Get new arrivals
    @GetMapping("/new-arrivals")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getNewArrivals(
            @PageableDefault(size = 8) Pageable pageable) {
        Page<ProductResponse> products = productService.getNewArrivals(pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // Get products by brand
    @GetMapping("/brand/{brand}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByBrand(
            @PathVariable String brand,
            @PageableDefault(size = 12) Pageable pageable) {
        Page<ProductResponse> products = productService.getProductsByBrand(brand, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // Get products by tag
    @GetMapping("/tag/{tag}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByTag(
            @PathVariable String tag,
            @PageableDefault(size = 12) Pageable pageable) {
        Page<ProductResponse> products = productService.getProductsByTag(tag, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // Get all brands
    @GetMapping("/filters/brands")
    public ResponseEntity<ApiResponse<List<String>>> getAllBrands() {
        List<String> brands = productService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.success(brands));
    }

    // Get all tags
    @GetMapping("/filters/tags")
    public ResponseEntity<ApiResponse<List<String>>> getAllTags() {
        List<String> tags = productService.getAllTags();
        return ResponseEntity.ok(ApiResponse.success(tags));
    }

    // Increment product view count
    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable UUID id) {
        productService.incrementProductViewCount(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
