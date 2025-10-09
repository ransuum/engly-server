package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repository.RefreshTokenRepository;
import com.engly.engly_server.security.cookiemanagement.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutHandlerServiceImpl implements LogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        var cookieUtil = new CookieUtils(request.getCookies());
        var authCookie = cookieUtil.getRefreshTokenCookie();

        if (StringUtils.isBlank(authCookie) &&
                !Objects.requireNonNull(authCookie).startsWith(TokenType.BEARER.name())) return;

        var refreshToken = Objects.requireNonNull(authCookie).substring(7);

        refreshTokenRepository.findByTokenAndRevokedIsFalse(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    cookieUtil.clearCookies(response);
                });
    }
}