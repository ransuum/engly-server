package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import module java.base;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT rt FROM RefreshToken rt JOIN FETCH rt.user WHERE rt.token = :token AND rt.revoked = false")
    Optional<RefreshToken> findByTokenAndRevokedIsFalse(String token);

    boolean existsByTokenAndRevokedIsFalse(String token);

    List<RefreshToken> findAllByExpiresAtBeforeOrRevokedIsTrue(Instant now);

    @Modifying
    @Query(value = "UPDATE refresh_tokens SET revoked = true WHERE token = :token AND revoked = false", nativeQuery = true)
    void updateRevokeByToken(String token);
}
