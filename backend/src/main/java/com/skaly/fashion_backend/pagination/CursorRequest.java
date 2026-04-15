package com.skaly.fashion_backend.pagination;

public class CursorRequest {
    private String cursor;
    private int limit;
    private String sortField;
    private SortDirection sortDirection;

    public CursorRequest() {
        this.limit = 20;
        this.sortDirection = SortDirection.DESC;
    }

    public CursorRequest(String cursor, int limit, String sortField, SortDirection sortDirection) {
        this.cursor = cursor;
        this.limit = limit;
        this.sortField = sortField;
        this.sortDirection = sortDirection;
    }

    // Getters and Setters
    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    public enum SortDirection {
        ASC, DESC
    }
}
