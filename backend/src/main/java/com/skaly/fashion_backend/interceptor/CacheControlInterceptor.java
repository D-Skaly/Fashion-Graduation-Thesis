package com.skaly.fashion_backend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CacheControlInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();

        // Set cache headers based on endpoint
        if (path.startsWith("/api/v1/products")) {
            // ProductEntity data - cache for 1 hour
            response.setHeader("Cache-Control", "public, max-age=3600");
        } else if (path.startsWith("/api/v1/categories")) {
            // Categories - cache for 24 hours
            response.setHeader("Cache-Control", "public, max-age=86400");
        } else if (path.startsWith("/api/v1/brands")) {
            // Brands - cache for 24 hours
            response.setHeader("Cache-Control", "public, max-age=86400");
        } else if (path.startsWith("/api/v1/auth") || path.startsWith("/api/v1/orders")) {
            // Auth and user-specific data - no caching
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }

        return true;
    }
}
