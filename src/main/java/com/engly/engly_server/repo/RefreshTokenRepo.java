package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    List<RefreshToken> findByExpiresAtBefore(Instant now);

    @Modifying
    @Transactional
    void deleteByExpiresAtBefore(Instant now);

    @Modifying
    @Transactional
    void deleteByRevokedTrueAndExpiresAtBefore(Instant now);
}
