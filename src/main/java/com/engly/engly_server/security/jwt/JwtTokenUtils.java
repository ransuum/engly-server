package com.engly.engly_server.security.jwt;

import com.engly.engly_server.exception.TokenGenerationException;
import com.engly.engly_server.exception.TokenNotFoundException;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.security.config.SecurityContextConfig;
import com.engly.engly_server.security.jwt.validation.CompositeTokenValidator;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtils {

    private final JwtDecoder jwtDecoder;
    private final CompositeTokenValidator tokenValidator;
    private final SecurityContextConfig securityContextConfig;

    public @Nullable String getUsername(@NonNull Jwt jwtToken) {
        return jwtToken.getSubject();
    }

    public Jwt decodeToken(@NonNull String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            throw new TokenGenerationException("Failed to decode JWT token", e);
        }
    }

    public JwtHelper loadUserDetails() {
        return jwt -> new UserDetailsImpl(Users.builder()
                .email(jwt.getSubject())
                .roles(jwt.getClaim("roles"))
                .build());
    }

    public Collection<GrantedAuthority> extractAuthorities(@NonNull Jwt jwt, @NonNull UserDetails userDetails) {
        Collection<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());

        Optional.ofNullable(jwt.getClaim("scope"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(scopeStr -> Arrays.stream(scopeStr.split(" "))
                        .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                        .forEach(authorities::add));

        return authorities;
    }

    public void authenticateToken(@NonNull Jwt token,
                                  @NonNull HttpServletRequest request,
                                  boolean isTokenValidInContext) {
        try {
            if (securityContextConfig.isAuthenticationEmpty()) {
                var userDetails = loadUserDetails().createUserDetailsFromJwtClaims(token);

                if (tokenValidator.validateToken(token, userDetails) && isTokenValidInContext)
                    securityContextConfig.setSecurityContext(token, userDetails, request);
            }
        } catch (Exception e) {
            throw new TokenNotFoundException("Error during token authentication " + e.getMessage());
        }
    }

    public Authentication createSocketAuthentication(@NonNull String token) {
        var jwt = decodeToken(token);
        var userDetails = loadUserDetails().createUserDetailsFromJwtClaims(jwt);

        if (!tokenValidator.validateToken(jwt, userDetails))
            throw new TokenGenerationException("Invalid JWT token");

        return new UsernamePasswordAuthenticationToken(userDetails, null, extractAuthorities(jwt, userDetails));
    }
}
