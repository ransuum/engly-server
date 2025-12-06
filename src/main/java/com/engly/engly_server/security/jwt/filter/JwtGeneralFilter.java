package com.engly.engly_server.security.jwt.filter;

import com.engly.engly_server.security.jwt.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Slf4j
public abstract class JwtGeneralFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final ObjectMapper objectMapper;

    protected JwtGeneralFilter(JwtTokenUtils jwtTokenUtils, ObjectMapper objectMapper) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException {
        try {
            var token = extractToken(request);

            if (StringUtils.isBlank(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            var jwt = jwtTokenUtils.decodeToken(token);
            jwtTokenUtils.authenticateToken(jwt, request, isTokenValidInContext(jwt));

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    protected abstract String extractToken(@NonNull HttpServletRequest request);

    protected abstract boolean isTokenValidInContext(@NonNull Jwt jwt);

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        var errorResponse = Map.of(
                "error", "Authentication failed",
                "message", e.getMessage(),
                "timestamp", Instant.now().toString()
        );

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
