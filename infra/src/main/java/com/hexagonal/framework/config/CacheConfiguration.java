package com.hexagonal.framework.config;

import com.hexagonal.application.port.out.CategoryCachePort;
import com.hexagonal.framework.cache.CaffeineCategoryCacheAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Bean
    public CategoryCachePort categoryCachePort(
            @Value("${cache.category.ttlSeconds:300}") long ttlSeconds,
            @Value("${cache.category.maxSize:1000}") long maxSize
    ) {
        return new CaffeineCategoryCacheAdapter(ttlSeconds, maxSize);
    }
}
