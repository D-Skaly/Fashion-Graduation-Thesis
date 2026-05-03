package com.skaly.fashion_backend.common.infrastructure.security;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthRateLimitFilter extends HttpFilter {
    
    private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    
    private RateLimiter getRateLimiter(String clientIp) {
        return limiters.computeIfAbsent(clientIp, ip -> 
            RateLimiter.of(ip + "-auth-limiter", 
                RateLimiterConfig.custom()
                    .limitRefreshPeriod(Duration.ofMinutes(1))
                    .limitForPeriod(5) // 5 attempts per minute
                    .timeoutDuration(Duration.ofMillis(100))
                    .build())
        );
    }
    
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {
        
        if (request.getRequestURI().contains("/api/v1/auth/")) {
            String clientIp = getClientIp(request);
            RateLimiter limiter = getRateLimiter(clientIp);
            
            if (!limiter.acquirePermission()) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("{\"error\": \"Too many requests. Please try again in 1 minute.\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
