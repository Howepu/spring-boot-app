package com.example.springbootapp.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация кэширования для приложения
 * Настраивает базовый CacheManager на основе ConcurrentHashMap
 */
@Configuration
public class CacheConfig implements CachingConfigurer {

    /**
     * Создает CacheManager для кэширования ответов API
     * 
     * @return CacheManager на основе ConcurrentHashMap
     */
    @Bean
    public CacheManager cacheManager() {
        // Создаем простой in-memory кэш менеджер с указанными кэшами
        return new ConcurrentMapCacheManager("insightsCache");
    }
}
