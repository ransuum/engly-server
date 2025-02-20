package com.engly.engly_server.service.impl;

import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repo.RefreshTokenRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LogoutHandlerService implements LogoutHandler {
    private final RefreshTokenRepo refreshTokenRepo;

    public LogoutHandlerService(RefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!authHeader.startsWith(TokenType.Bearer.name())) return;

        final String refreshToken = authHeader.substring(7);

        var storedRefreshToken = refreshTokenRepo.findByRefreshToken(refreshToken)
                .map(token -> {
                    token.setRevoked(true);
                    refreshTokenRepo.save(token);
                    return token;
                })
                .orElse(null);
    }
}