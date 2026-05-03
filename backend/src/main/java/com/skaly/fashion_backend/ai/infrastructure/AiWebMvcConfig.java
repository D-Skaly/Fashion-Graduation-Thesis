package com.skaly.fashion_backend.ai.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AiWebMvcConfig implements WebMvcConfigurer {

    private final AiChatRateLimitInterceptor aiChatRateLimitInterceptor;

    public AiWebMvcConfig(AiChatRateLimitInterceptor aiChatRateLimitInterceptor) {
        this.aiChatRateLimitInterceptor = aiChatRateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(aiChatRateLimitInterceptor)
                .addPathPatterns("/api/v1/ai/chat");
    }
}
