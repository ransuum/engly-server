package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.security.cookiemanagement.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutHandlerServiceImpl implements LogoutHandler {
    private final RefreshTokenRepo refreshTokenRepo;

    @Value("${app.backend-cookie.url}")
    private String url;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final var authCookie = new CookieUtils(request.getCookies()).getRefreshTokenCookie();

        if (authCookie != null && !authCookie.startsWith(TokenType.Bearer.name())) return;

        final var refreshToken = Objects.requireNonNull(authCookie).substring(7);

        refreshTokenRepo.findByTokenAndRevokedIsFalse(refreshToken)
                .map(token -> {
                    token.setRevoked(true);
                    return refreshTokenRepo.save(token);
                }).orElse(null);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies).forEach(cookie -> {
                Cookie toDelete = new Cookie(cookie.getName(), "");
                toDelete.setPath(cookie.getPath() != null ? cookie.getPath() : "/");
                if (cookie.getDomain() != null) toDelete.setDomain(cookie.getDomain());
                toDelete.setMaxAge(0);
                toDelete.setHttpOnly(cookie.isHttpOnly());
                toDelete.setSecure(cookie.getSecure());

                response.addCookie(toDelete);
            });
        }
    }
}