package com.skaly.fashion_backend.common.infrastructure.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * Interceptor to add correlation ID to requests for tracing.
 * Configured in WebMvcConfig.
 */
@RequiredArgsConstructor
public class CorrelationIdInterceptor implements HandlerInterceptor {
    
    private final String headerName;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = request.getHeader(headerName);
        
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // Add to request attribute
        request.setAttribute("correlationId", correlationId);
        
        // Add to response header
        response.setHeader("X-Correlation-ID", correlationId);
        
        // Optionally set to MDC for logging
        org.slf4j.MDC.put("correlationId", correlationId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Clear MDC after request
        org.slf4j.MDC.remove("correlationId");
    }
}
