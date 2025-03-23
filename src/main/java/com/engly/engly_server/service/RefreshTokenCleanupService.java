package com.engly.engly_server.service;

@FunctionalInterface
public interface RefreshTokenCleanupService {
    void cleanupExpiredAndRevokedTokens();
}
