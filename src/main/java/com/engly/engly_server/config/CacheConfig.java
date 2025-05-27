package com.engly.engly_server.config;

import com.engly.engly_server.utils.cache.CacheName;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        final Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(300, TimeUnit.SECONDS);

        final List<CaffeineCache> caches = CacheName.CACHES.stream()
                .map(name -> new CaffeineCache(name, caffeineBuilder.build()))
                .toList();

        final var cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
