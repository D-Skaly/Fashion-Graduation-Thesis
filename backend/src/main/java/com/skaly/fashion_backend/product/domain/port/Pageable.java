package com.skaly.fashion_backend.product.domain.port;

/**
 * Đối tượng phân trang thuần Java - thay thế cho org.springframework.data.domain.Pageable
 * Tuân thủ Clean Architecture: Domain không phụ thuộc Spring.
 */
public class Pageable {
    private final int pageNumber;
    private final int pageSize;
    
    public Pageable(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
    
    public static Pageable of(int pageNumber, int pageSize) {
        return new Pageable(pageNumber, pageSize);
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public int getOffset() {
        return pageNumber * pageSize;
    }
}