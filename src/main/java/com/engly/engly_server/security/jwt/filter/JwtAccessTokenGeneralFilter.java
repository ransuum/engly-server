package com.engly.engly_server.security.jwt.filter;

import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.security.config.SecurityContextConfig;
import com.engly.engly_server.security.jwt.JwtTokenUtils;
import com.engly.engly_server.security.jwt.validation.CompositeTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessTokenGeneralFilter extends JwtGeneralFilter {

    protected JwtAccessTokenGeneralFilter(SecurityContextConfig securityContextConfig,
                                          CompositeTokenValidator compositeTokenValidator,
                                          JwtTokenUtils jwtTokenUtils) {
        super(securityContextConfig, compositeTokenValidator, jwtTokenUtils);
    }

    @Override
    protected String extractToken(HttpServletRequest request) {
        final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(TokenType.Bearer.name())) return null;

        return authHeader.substring(7);
    }

    @Override
    protected boolean isTokenValidInContext(Jwt jwt) {
        return true;
    }
}
