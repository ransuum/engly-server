package com.engly.engly_server.security.jwt.filter;

import com.engly.engly_server.security.config.SecurityContextConfig;
import com.engly.engly_server.security.jwt.JwtTokenUtils;
import com.engly.engly_server.security.jwt.validation.CompositeTokenValidator;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Slf4j
public abstract class JwtGeneralFilter extends OncePerRequestFilter {
    private final SecurityContextConfig securityContextConfig;
    private final CompositeTokenValidator compositeTokenValidator;
    private final JwtTokenUtils jwtTokenUtils;

    protected JwtGeneralFilter(SecurityContextConfig securityContextConfig,
                               CompositeTokenValidator compositeTokenValidator,
                               JwtTokenUtils jwtTokenUtils) {
        this.securityContextConfig = securityContextConfig;
        this.compositeTokenValidator = compositeTokenValidator;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException {
        try {
            log.info("[{}:doFilterInternal] :: Started", getClass().getSimpleName());
            log.info("[{}:doFilterInternal] Filtering the Http Request: {}",
                    getClass().getSimpleName(), request.getRequestURI());

            final String token = extractToken(request);

            if (token == null) {
                log.debug("[{}:doFilterInternal] No valid token found, skipping authentication",
                        getClass().getSimpleName());
                filterChain.doFilter(request, response);
                return;
            }

            authenticateToken(token, request);

            filterChain.doFilter(request, response);
            log.info("[{}:doFilterInternal] Completed", getClass().getSimpleName());
        } catch (Exception e) {
            log.error("[{}:doFilterInternal] Exception due to: {}",
                    getClass().getSimpleName(), e.getMessage());
            handleException(response, e);
        }
    }

    protected abstract String extractToken(HttpServletRequest request);

    protected abstract boolean isTokenValidInContext(Jwt jwt);

    private void authenticateToken(String token, HttpServletRequest request) {
        try {
            final var jwt = jwtTokenUtils.decodeToken(token);
            final var username = jwtTokenUtils.getUsername(jwt);

            if (FieldUtil.isValid(username) && securityContextConfig.isAuthenticationEmpty()) {
                var userDetails = jwtTokenUtils.loadUserDetails(username);

                if (compositeTokenValidator.validateToken(jwt, userDetails) && isTokenValidInContext(jwt)) {
                    securityContextConfig.setSecurityContext(jwt, userDetails, request);
                    log.debug("Successfully authenticated user: {}", username);
                } else {
                    log.debug("Token validation failed for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Error during token authentication", e);
        }
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        log.error("[{}:doFilterInternal] Authentication failed: {}",
                getClass().getSimpleName(), e.getMessage(), e);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        var errorResponse = Map.of(
                "error", "Authentication failed",
                "message", e.getMessage(),
                "timestamp", Instant.now().toString()
        );

        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}
