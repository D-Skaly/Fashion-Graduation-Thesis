package com.skaly.fashion_backend.common.infrastructure.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class CorrelationIdInterceptor implements HandlerInterceptor {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_LOG_KEY = "correlationId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // Add to MDC for logging
        MDC.put(CORRELATION_ID_LOG_KEY, correlationId);
        
        // Add to response header
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        
        // Add to request attribute for use in controllers
        request.setAttribute(CORRELATION_ID_LOG_KEY, correlationId);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Clean up MDC
        MDC.remove(CORRELATION_ID_LOG_KEY);
    }
}