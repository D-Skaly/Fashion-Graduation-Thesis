package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.common.domain.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class AiChatRateLimitInterceptor implements HandlerInterceptor {

    private final AiAssistantProperties properties;
    private final ObjectMapper objectMapper;
    private final Counter blockedCounter;
    private final Map<String, Deque<Long>> requestWindows = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public AiChatRateLimitInterceptor(AiAssistantProperties properties, ObjectMapper objectMapper, MeterRegistry meterRegistry) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.blockedCounter = Counter.builder("ai.chat.rate_limited")
                .description("Total AI chat requests blocked by rate limiter")
                .register(meterRegistry);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!properties.rateLimit().enabled()) {
            return true;
        }

        String key = resolveClientKey(request);
        long now = System.currentTimeMillis();
        long windowStart = now - properties.rateLimit().windowSeconds() * 1000L;

        Deque<Long> timestamps = requestWindows.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());

        lock.lock();
        try {
            while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= properties.rateLimit().maxRequests()) {
                blockedCounter.increment();
                response.setStatus(429);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(objectMapper.writeValueAsString(
                        ApiResponse.error(429, "Too many AI chat requests. Please try again later.")));
                return false;
            }

            timestamps.offerLast(now);
        } finally {
            lock.unlock();
        }

        return true;
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        return request.getRemoteAddr();
    }
}
