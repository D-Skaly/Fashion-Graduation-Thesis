package com.skaly.fashion_backend.common.infrastructure.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to add Cache-Control headers to responses.
 * Configured in WebMvcConfig.
 */
@RequiredArgsConstructor
public class CacheControlInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Add Cache-Control header
        String method = request.getMethod();
        if ("GET".equals(method)) {
            response.setHeader("Cache-Control", "private, max-age=3600");
        } else {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        }
        return true;
    }
}
