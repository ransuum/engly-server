package com.engly.engly_server.security.jwt.filter;

import com.engly.engly_server.security.jwt.JwtTokenUtils;
import com.engly.engly_server.service.common.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessTokenGeneralFilter extends JwtGeneralFilter {
    private final UserService  userService;

    protected JwtAccessTokenGeneralFilter(JwtTokenUtils jwtTokenUtils, ObjectMapper objectMapper, UserService userService) {
        super(jwtTokenUtils, objectMapper);
        this.userService = userService;
    }

    @Override
    protected String extractToken(@NonNull HttpServletRequest request) {
        final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        return (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7) : null;
    }

    @Override
    protected boolean isTokenValidInContext(@NonNull Jwt jwt) {
        return userService.existsById(jwt.getClaim("userId"));
    }
}
