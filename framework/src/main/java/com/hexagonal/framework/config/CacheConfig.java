package com.hexagonal.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Cache Configuration
 * 
 * In-Memory Cache: Spring Boot built-in cache (ConcurrentMapCacheManager)
 * Redis Cache: For future use when Redis is available
 * 
 * To use Redis in the future:
 * 1. Set spring.cache.type=redis in application.yaml
 * 2. Configure Redis connection properties
 * 3. Redis cache manager will be used automatically
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * In-Memory Cache Manager (Default)
     * Used when Redis is not configured
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "simple", matchIfMissing = true)
    public CacheManager inMemoryCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "products",
            "customers",
            "categories",
            "orders",
            "carts"
        ));
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }

    /**
     * Redis Cache Manager (For future use)
     * Activated when spring.cache.type=redis
     * 
     * To enable Redis:
     * 1. Add Redis connection properties to application.yaml:
     *    spring:
     *      data:
     *        redis:
     *          host: localhost
     *          port: 6379
     *      cache:
     *        type: redis
     * 
     * 2. Start Redis server (docker run -d -p 6379:6379 redis:latest)
     */
    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("products", config.entryTtl(Duration.ofHours(2)))
            .withCacheConfiguration("customers", config.entryTtl(Duration.ofHours(1)))
            .withCacheConfiguration("categories", config.entryTtl(Duration.ofDays(1)))
            .withCacheConfiguration("orders", config.entryTtl(Duration.ofMinutes(30)))
            .withCacheConfiguration("carts", config.entryTtl(Duration.ofMinutes(15)))
            .transactionAware()
            .build();
    }
}

