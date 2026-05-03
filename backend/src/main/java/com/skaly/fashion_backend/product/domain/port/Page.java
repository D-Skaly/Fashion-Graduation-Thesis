package com.skaly.fashion_backend.product.domain.port;

import java.util.List;

/**
 * Đối tượng trang thuần Java - thay thế cho org.springframework.data.domain.Page
 * Tuân thủ Clean Architecture: Domain không phụ thuộc Spring.
 */
public class Page<T> {
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    
    public Page(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
    }
    
    public List<T> getContent() {
        return content;
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public boolean hasContent() {
        return !content.isEmpty();
    }
    
    public boolean isFirst() {
        return pageNumber == 0;
    }
    
    public boolean isLast() {
        return pageNumber >= totalPages - 1;
    }
}