package com.engly.engly_server.security.jwt.filter;

import com.engly.engly_server.exception.ExceptionResponse;
import com.engly.engly_server.security.jwt.JwtTokenUtils;
import com.engly.engly_server.security.rsa.RSAKeyRecord;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;

@Slf4j
@RequiredArgsConstructor
public abstract class JwtGeneralFilter extends OncePerRequestFilter {
    protected final RSAKeyRecord rsaKeyRecord;
    protected final JwtTokenUtils jwtTokenUtils;

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
        if (!jwtTokenUtils.isSecurityContextEmpty()) return;

        final JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
        final var jwt = jwtDecoder.decode(token);
        final var userName = jwtTokenUtils.getUsername(jwt);

        if (!userName.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = jwtTokenUtils.userDetails(userName);

            if (jwtTokenUtils.isTokenValid(jwt, userDetails) && isTokenValidInContext(jwt))
                setSecurityContext(jwt, userDetails, request);
        }
    }

    private void setSecurityContext(Jwt jwt, UserDetails userDetails, HttpServletRequest request) {
        Collection<GrantedAuthority> authorities = new LinkedList<>(userDetails.getAuthorities());

        final var scope = jwt.getClaimAsString("scope");
        if (FieldUtil.isValid(scope))
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope.toUpperCase()));

        jwtTokenUtils.securityContextSetter(userDetails, authorities, request);
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        new ExceptionResponse("Session Problem", HttpStatus.NOT_ACCEPTABLE.value(),
                e.getMessage(), LocalDateTime.now())
                .responseConfiguration(response)
                .throwException(response.getOutputStream());
    }
}
