package com.engly.engly_server.security.jwt.service;

import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.security.jwt.JwtHolder;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {
    private final JwtTokenGenerator jwtTokenGenerator;

    public JwtHolder createAuthObject(Users users) {
        final var authentication = jwtTokenGenerator.createAuthenticationObject(users);
        final var accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        final var refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
        return new JwtHolder(refreshToken, accessToken);
    }

    public JwtHolder authenticateData(Authentication authentication, HttpServletResponse response) {
        final var refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
        final var accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        jwtTokenGenerator.creatRefreshTokenCookie(response, refreshToken);
        return new JwtHolder(refreshToken, accessToken);
    }

    public RefreshToken createRefreshToken(Users users, String refreshToken) {
        return RefreshToken.builder()
                .user(users)
                .refreshToken(refreshToken)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(25, ChronoUnit.DAYS))
                .revoked(false)
                .build();
    }
}
