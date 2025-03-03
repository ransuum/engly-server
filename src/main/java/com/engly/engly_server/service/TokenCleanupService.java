package com.engly.engly_server.service;

@FunctionalInterface
public interface TokenCleanupService {
    void cleanupExpiredTokens();
}
