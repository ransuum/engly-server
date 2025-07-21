package com.engly.engly_server.config;

import com.engly.engly_server.utils.cache.CacheName;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Slf4j
@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    @Primary
    CacheManager cacheManager() {
        var cacheManager = new SimpleCacheManager();

        final var caches = CacheName.CACHES.stream()
                .map(this::createCacheForName)
                .toList();

        cacheManager.setCaches(caches);
        log.info("Configured cache manager with {} caches", caches.size());
        return cacheManager;
    }

    private CaffeineCache createCacheForName(String cacheName) {
        final var spec = getCacheSpec(cacheName);

        var builder = Caffeine.newBuilder()
                .maximumSize(spec.maxSize())
                .expireAfterWrite(spec.expireAfterWrite())
                .expireAfterAccess(spec.expireAfterAccess())
                .softValues()
                .recordStats();

        return new CaffeineCache(cacheName, builder.build());
    }

    private CacheSpec getCacheSpec(String cacheName) {
        return switch (cacheName) {
            case CacheName.USER_ID, CacheName.USER_BY_EMAIL, CacheName.USER_ID_BY_EMAIL, CacheName.USERNAME_BY_EMAIL,
                 CacheName.USER_BY_EMAIL_DTO -> new CacheSpec(50, Duration.ofMinutes(10), Duration.ofMinutes(3));
            case CacheName.USER_PROFILES -> new CacheSpec(25, Duration.ofMinutes(5), Duration.ofMinutes(2));
            case CacheName.ALL_USER -> new CacheSpec(1, Duration.ofMinutes(1), Duration.ofSeconds(30));
            case CacheName.USERNAME_AVAILABILITY, CacheName.EMAIL_AVAILABILITY ->
                    new CacheSpec(25, Duration.ofMinutes(1), Duration.ofSeconds(30));
            case CacheName.ROOM_ID, CacheName.ROOM_DTO_ID, CacheName.ROOM_ENTITY_ID ->
                    new CacheSpec(40, Duration.ofMinutes(5), Duration.ofMinutes(2));
            case CacheName.CATEGORY_ID, CacheName.CATEGORY_ENTITY_ID, CacheName.CATEGORY_NAME ->
                    new CacheSpec(20, Duration.ofMinutes(15), Duration.ofMinutes(5));
            case CacheName.MESSAGE_ID -> new CacheSpec(40, Duration.ofMinutes(3), Duration.ofMinutes(1));
            case CacheName.MESSAGES_BY_ROOM, CacheName.MESSAGES_BY_ROOM_NATIVE, CacheName.MESSAGES_BY_ROOM_CURSOR ->
                    new CacheSpec(10, Duration.ofMinutes(2), Duration.ofSeconds(30));
            default -> new CacheSpec(15, Duration.ofMinutes(3), Duration.ofMinutes(1));
        };
    }

    private record CacheSpec(
            long maxSize,
            Duration expireAfterWrite,
            Duration expireAfterAccess
    ) {
    }
}
