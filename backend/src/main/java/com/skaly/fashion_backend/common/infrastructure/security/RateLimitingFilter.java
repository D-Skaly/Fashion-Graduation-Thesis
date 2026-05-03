package com.skaly.fashion_backend.common.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                       FilterChain filterChain) throws ServletException, IOException {
        
        String clientId = getClientId(request);
        String endpoint = request.getRequestURI();
        String key = rateLimitingService.getClientKey(clientId, endpoint);
        
        // Skip rate limiting for health check and swagger
        if (endpoint.contains("/health") || endpoint.contains("/swagger") || endpoint.contains("/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Different limits for different endpoints
        int limit = getLimitForEndpoint(endpoint);
        
        if (!rateLimitingService.isAllowed(key, limit, 60)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
            errorResponse.put("error", "Too Many Requests");
            errorResponse.put("message", "Rate limit exceeded. Please try again later.");
            errorResponse.put("path", endpoint);
            
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    private String getClientId(HttpServletRequest request) {
        // Try to get user ID from JWT if authenticated
        String userId = request.getAttribute("userId") != null 
                ? request.getAttribute("userId").toString() 
                : null;
        
        if (userId != null) {
            return "user:" + userId;
        }
        
        // Fallback to IP address
        String ip = request.getRemoteAddr();
        return "ip:" + ip;
    }

    private int getLimitForEndpoint(String endpoint) {
        // Higher limit for public endpoints
        if (endpoint.contains("/api/v1/products") || endpoint.contains("/api/v1/categories")) {
            return 200;
        }
        
        // Lower limit for authentication endpoints
        if (endpoint.contains("/api/v1/auth")) {
            return 10;
        }
        
        // AI chat endpoint - very strict limit
        if (endpoint.contains("/api/v1/ai")) {
            return 20;
        }
        
        // Default limit
        return 100;
    }
}