package com.skaly.fashion_backend.common.infrastructure.config;

import com.skaly.fashion_backend.common.infrastructure.interceptor.CacheControlInterceptor;
import com.skaly.fashion_backend.common.infrastructure.interceptor.CorrelationIdInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final CacheControlInterceptor cacheControlInterceptor;
    private final CorrelationIdInterceptor correlationIdInterceptor;

    public WebMvcConfig(CacheControlInterceptor cacheControlInterceptor, CorrelationIdInterceptor correlationIdInterceptor) {
        this.cacheControlInterceptor = cacheControlInterceptor;
        this.correlationIdInterceptor = correlationIdInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Correlation ID interceptor - apply to all requests
        registry.addInterceptor(correlationIdInterceptor)
                .addPathPatterns("/**");

        // Cache control interceptor - apply to API endpoints
        registry.addInterceptor(cacheControlInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/v1/auth/**", "/api/v1/orders/**");
    }
}