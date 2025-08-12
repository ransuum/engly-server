package com.engly.engly_server.security.jwt.filter;

import com.engly.engly_server.repository.RefreshTokenRepository;
import com.engly.engly_server.security.config.SecurityContextConfig;
import com.engly.engly_server.security.cookiemanagement.CookieUtils;
import com.engly.engly_server.security.jwt.JwtTokenUtils;
import com.engly.engly_server.security.jwt.validation.CompositeTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtRefreshTokenGeneralFilter extends JwtGeneralFilter {
    private final RefreshTokenRepository refreshTokenRepository;

    protected JwtRefreshTokenGeneralFilter(SecurityContextConfig securityContextConfig, CompositeTokenValidator compositeTokenValidator,
                                           JwtTokenUtils jwtTokenUtils, RefreshTokenRepository refreshTokenRepository) {
        super(securityContextConfig, compositeTokenValidator, jwtTokenUtils);
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Override
    protected String extractToken(HttpServletRequest request) {
        return new CookieUtils(request.getCookies()).getRefreshTokenCookie();
    }

    @Override
    protected boolean isTokenValidInContext(Jwt jwt) {
        return refreshTokenRepository.findByTokenAndRevokedIsFalse(jwt.getTokenValue())
                .isPresent();
    }
}
