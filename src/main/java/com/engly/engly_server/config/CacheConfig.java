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
        log.info("Configured optimized cache manager with {} caches, total estimated memory: {}MB",
                caches.size(), estimateTotalMemoryUsage());
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
            case USER_BY_EMAIL
                    -> new CacheSpec(25, Duration.ofMinutes(3), Duration.ofMinutes(1));

            case USER_ID_BY_EMAIL, USERNAME_BY_EMAIL, USER_EXISTS_BY_ID
                    -> new CacheSpec(15, Duration.ofMinutes(2), Duration.ofSeconds(30));

            case USER_ID, USER_ENTITY_ID
                    -> new CacheSpec(20, Duration.ofMinutes(4), Duration.ofMinutes(1));

            case USER_PROFILES, USER_SETTINGS
                    -> new CacheSpec(10, Duration.ofMinutes(3), Duration.ofSeconds(45));

            case ALL_USER
                    -> new CacheSpec(2, Duration.ofMinutes(1), Duration.ofSeconds(15));

            case ROOM_DTO_ID, ROOM_ENTITY_ID
                    -> new CacheSpec(15, Duration.ofMinutes(2), Duration.ofSeconds(45));

            case ROOMS_BY_CATEGORY, ROOMS_BY_CRITERIA
                    -> new CacheSpec(5, Duration.ofSeconds(90), Duration.ofSeconds(20));

            case ALL_CATEGORIES
                    -> new CacheSpec(1, Duration.ofMinutes(10), Duration.ofMinutes(5));

            case MESSAGE_ID
                    -> new CacheSpec(20, Duration.ofMinutes(2), Duration.ofMinutes(1));

            case MESSAGES_BY_ROOM_NATIVE, MESSAGES_BY_CRITERIA
                    -> new CacheSpec(3, Duration.ofSeconds(45), Duration.ofSeconds(10));

            case MESSAGE_COUNT_BY_ROOM
                    -> new CacheSpec(10, Duration.ofSeconds(30), Duration.ofSeconds(15));

            case PARTICIPANTS_BY_ROOM, PARTICIPANT_EXISTS
                    -> new CacheSpec(15, Duration.ofMinutes(1), Duration.ofSeconds(30));

            case MESSAGE_READ_STATUS, USERS_WHO_READ_MESSAGE
                    -> new CacheSpec(10, Duration.ofSeconds(30), Duration.ofSeconds(10));

            default -> new CacheSpec(5, Duration.ofMinutes(1), Duration.ofSeconds(30));
        };
    }

    private int estimateTotalMemoryUsage() {
        return CacheName.CACHES.stream()
                .mapToInt(name -> (int) getCacheSpec(name).maxSize())
                .sum() / 1024;
    }

    private record CacheSpec(
            long maxSize,
            Duration expireAfterWrite,
            Duration expireAfterAccess
    ) {
    }
}
