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

import static com.engly.engly_server.utils.cache.CacheName.*;

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
            case USER_ID, USER_BY_EMAIL, USER_ID_BY_EMAIL, USERNAME_BY_EMAIL, USER_BY_EMAIL_DTO
                    -> new CacheSpec(50, Duration.ofMinutes(10), Duration.ofMinutes(3));
            case USER_PROFILES -> new CacheSpec(25, Duration.ofMinutes(5), Duration.ofMinutes(2));
            case ALL_USER, USER_SETTINGS -> new CacheSpec(1, Duration.ofMinutes(1), Duration.ofSeconds(30));
            case USERNAME_AVAILABILITY, EMAIL_AVAILABILITY -> new CacheSpec(25, Duration.ofMinutes(1), Duration.ofSeconds(30));
            case ROOM_ID, ROOM_DTO_ID, ROOM_ENTITY_ID -> new CacheSpec(40, Duration.ofMinutes(5), Duration.ofMinutes(2));
            case CATEGORY_ID, CATEGORY_ENTITY_ID, CATEGORY_NAME -> new CacheSpec(20, Duration.ofMinutes(15), Duration.ofMinutes(5));
            case MESSAGE_ID -> new CacheSpec(40, Duration.ofMinutes(3), Duration.ofMinutes(1));
            case MESSAGES_BY_ROOM, MESSAGES_BY_ROOM_NATIVE, MESSAGES_BY_ROOM_CURSOR -> new CacheSpec(10, Duration.ofMinutes(2), Duration.ofSeconds(30));
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
