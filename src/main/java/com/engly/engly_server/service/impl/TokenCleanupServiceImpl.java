package com.engly.engly_server.service.impl;

import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.service.TokenCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
public class TokenCleanupServiceImpl implements TokenCleanupService {
    private final RefreshTokenRepo refreshTokenRepo;

    public TokenCleanupServiceImpl(RefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @Transactional
    @Scheduled(cron = "0 0 0/4 * * ?")
    public void cleanupExpiredTokens() {
        log.info("[TokenCleanupService:cleanupExpiredTokens] Starting token cleanup");
        Instant now = Instant.now();

        try {
            int count = refreshTokenRepo.findByExpiresAtBefore(now).size();
            refreshTokenRepo.deleteByExpiresAtBefore(now);
            log.info("[TokenCleanupService:cleanupExpiredTokens] Deleted {} expired tokens", count);
        } catch (Exception e) {
            log.error("[TokenCleanupService:cleanupExpiredTokens] Error during cleanup: {}", e.getMessage());
        }
    }
}
