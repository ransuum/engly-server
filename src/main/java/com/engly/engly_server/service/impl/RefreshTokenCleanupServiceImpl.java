package com.engly.engly_server.service.impl;

import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.service.RefreshTokenCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
public class RefreshTokenCleanupServiceImpl implements RefreshTokenCleanupService {
    private final RefreshTokenRepo refreshTokenRepo;

    public RefreshTokenCleanupServiceImpl(RefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @Override
    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void cleanupExpiredAndRevokedTokens() {
        Instant now = Instant.now();

        refreshTokenRepo.findAllByExpiresAtBeforeOrRevokedIsTrue(now).forEach(refreshToken -> {
            refreshTokenRepo.delete(refreshToken);
            log.info("Deleted refresh token {}", refreshToken);
        });
    }
}
