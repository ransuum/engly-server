package com.engly.engly_server.security.jwt.filter;

import com.engly.engly_server.repository.RefreshTokenRepository;
import com.engly.engly_server.security.cookiemanagement.CookieUtils;
import com.engly.engly_server.security.jwt.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtRefreshTokenGeneralFilter extends JwtGeneralFilter {
    private final RefreshTokenRepository refreshTokenRepository;

    protected JwtRefreshTokenGeneralFilter(JwtTokenUtils jwtTokenUtils,ObjectMapper objectMapper,
                                           RefreshTokenRepository refreshTokenRepository) {
        super(jwtTokenUtils, objectMapper);
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Override
    protected String extractToken(@NonNull HttpServletRequest request) {
        return new CookieUtils(request.getCookies()).getRefreshTokenCookie();
    }

    @Override
    protected boolean isTokenValidInContext(@NonNull Jwt jwt) {
        return refreshTokenRepository.existsByTokenAndRevokedIsFalse(jwt.getTokenValue());
    }
}
