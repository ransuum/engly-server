package com.engly.engly_server.security.jwt;

import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.security.rsa.RSAKeyRecord;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessTokenGeneralFilter extends JwtGeneralFilter {

    public JwtAccessTokenGeneralFilter(RSAKeyRecord rsaKeyRecord, JwtTokenUtils jwtTokenUtils) {
        super(rsaKeyRecord, jwtTokenUtils);
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
