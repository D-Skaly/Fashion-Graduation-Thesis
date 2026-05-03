package com.skaly.fashion_backend.common.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheControlConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Static resources - cache for 1 year
        registry.addResourceHandler("/static/**", "/images/**", "/css/**", "/js/**")
                .addResourceLocations("classpath:/static/", "classpath:/images/", "classpath:/css/", "classpath:/js/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic());

        // API responses - cache for different durations based on endpoint type
        registry.addResourceHandler("/api/v1/products/**")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());

        registry.addResourceHandler("/api/v1/categories/**")
                .setCacheControl(CacheControl.maxAge(24, TimeUnit.HOURS).cachePublic());

        registry.addResourceHandler("/api/v1/brands/**")
                .setCacheControl(CacheControl.maxAge(24, TimeUnit.HOURS).cachePublic());
    }
}