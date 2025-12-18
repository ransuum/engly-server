package com.engly.engly_server.config;

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
import java.util.Arrays;
import java.util.List;

import static com.engly.engly_server.utils.CacheName.*;

@Slf4j
@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    @Primary
    CacheManager cacheManager() {
        final List<String> caches = Arrays.asList(
                USER_ID, ALL_USER, USER_BY_EMAIL, USER_PROFILES, USER_ID_BY_EMAIL, USERNAME_BY_EMAIL,ROOM_DTO_ID,
                USER_SETTINGS, ROOMS_BY_CATEGORY, ROOM_ENTITY_ID, ROOMS_BY_CRITERIA, COUNT_PARTICIPANTS,
                CATEGORY_ENTITY_ID, CATEGORY_NAME, ALL_CATEGORIES, MESSAGE_ID, CATEGORY_ID_BY_NAME, ROOM_SHORT_ID,
                MESSAGES_BY_ROOM_NATIVE, MESSAGE_COUNT_BY_ROOM, MESSAGES_BY_CRITERIA, PARTICIPANTS_BY_ROOM,
                PARTICIPANT_EXISTS, MESSAGE_READ_STATUS, USER_EXISTS_BY_ID, USER_ENTITY_ID
        );
        var cacheManager = new SimpleCacheManager();

        final var cachesList = caches.stream()
                .map(this::createCacheForName)
                .toList();

        cacheManager.setCaches(cachesList);
        log.info("Configured optimized cache manager with {} caches, total estimated memory: {}MB",
                caches.size(), estimateTotalMemoryUsage(caches));
        return cacheManager;
    }

    private CaffeineCache createCacheForName(String cacheName) {
        final var spec = getCacheSpec(cacheName);

        return new CaffeineCache(cacheName, Caffeine.newBuilder()
                .maximumSize(spec.maxSize())
                .expireAfterWrite(spec.expireAfterWrite())
                .expireAfterAccess(spec.expireAfterAccess())
                .build());
    }

    private CacheSpec getCacheSpec(String cacheName) {
        return switch (cacheName) {
            case USER_BY_EMAIL, USER_ID, USERNAME_BY_EMAIL, USER_ID_BY_EMAIL ->
                    new CacheSpec(10, Duration.ofMinutes(3), Duration.ofMinutes(1));

            case USER_PROFILES, USER_SETTINGS -> new CacheSpec(5, Duration.ofMinutes(5), Duration.ofMinutes(2));

            case ALL_USER -> new CacheSpec(1, Duration.ofMinutes(2), Duration.ofMinutes(1));

            case ROOM_DTO_ID, ROOM_ENTITY_ID, ROOMS_BY_CATEGORY, ROOMS_BY_CRITERIA, ROOM_SHORT_ID ->
                    new CacheSpec(5, Duration.ofMinutes(2), Duration.ofMinutes(1));

            case MESSAGE_ID, MESSAGE_COUNT_BY_ROOM -> new CacheSpec(10, Duration.ofMinutes(2), Duration.ofMinutes(1));

            case PARTICIPANTS_BY_ROOM, PARTICIPANT_EXISTS ->
                    new CacheSpec(5, Duration.ofMinutes(1), Duration.ofSeconds(30));

            case MESSAGE_READ_STATUS ->
                    new CacheSpec(5, Duration.ofSeconds(45), Duration.ofSeconds(20));

            case ALL_CATEGORIES -> new CacheSpec(1, Duration.ofMinutes(10), Duration.ofMinutes(5));

            default -> new CacheSpec(3, Duration.ofMinutes(1), Duration.ofSeconds(30));
        };
    }

    private int estimateTotalMemoryUsage(List<String> caches) {
        return caches.stream()
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
