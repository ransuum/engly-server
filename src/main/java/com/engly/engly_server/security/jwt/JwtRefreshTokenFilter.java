package com.engly.engly_server.security.jwt;

import com.engly.engly_server.exception.ApiErrorResponse;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.security.rsa.RSAKeyRecord;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
public class JwtRefreshTokenFilter extends OncePerRequestFilter {
    private final RSAKeyRecord rsaKeyRecord;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenRepo refreshTokenRepo;

    public JwtRefreshTokenFilter(RSAKeyRecord rsaKeyRecord, JwtTokenUtils jwtTokenUtils, RefreshTokenRepo refreshTokenRepo) {
        this.rsaKeyRecord = rsaKeyRecord;
        this.jwtTokenUtils = jwtTokenUtils;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("[JwtRefreshTokenFilter:doFilterInternal] :: Started ");
            log.info("[JwtRefreshTokenFilter:doFilterInternal]Filtering the Http Request:{}", request.getRequestURI());
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = getString(request, authHeader);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            final JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
            final Jwt jwtRefreshToken = jwtDecoder.decode(token);
            final String userName = jwtTokenUtils.getUserName(jwtRefreshToken);

            if (!userName.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                final var isRefreshTokenValidInDatabase = refreshTokenRepo.findByRefreshToken(jwtRefreshToken.getTokenValue())
                        .map(refreshTokenEntity -> !refreshTokenEntity.isRevoked())
                        .orElse(false);
                UserDetails userDetails = jwtTokenUtils.userDetails(userName);
                if (jwtTokenUtils.isTokenValid(jwtRefreshToken, userDetails) && isRefreshTokenValidInDatabase) {
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken createdToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    createdToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(createdToken);
                    SecurityContextHolder.setContext(securityContext);
                }
            }
            log.info("[JwtRefreshTokenFilter:doFilterInternal] Completed");
        } catch (JwtValidationException jwtValidationException) {
            log.error("[JwtRefreshTokenFilter:doFilterInternal] Exception due to :{}", jwtValidationException.getMessage());
            new ApiErrorResponse("Session Problem", HttpStatus.NOT_ACCEPTABLE.value(),
                    jwtValidationException.getMessage(), LocalDateTime.now())
                    .responseConfiguration(response)
                    .throwException(response.getOutputStream());
        }

        filterChain.doFilter(request, response);
    }

    private static String getString(HttpServletRequest request, String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer "))
            return authHeader.substring(7);
         else {
            final Cookie[] cookies = request.getCookies();
            if (cookies == null) return null;
            return Arrays.stream(cookies)
                    .filter(cookie -> "refresh_token".equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }
    }
}
