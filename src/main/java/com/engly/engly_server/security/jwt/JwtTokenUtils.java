package com.engly.engly_server.security.jwt;

import com.engly.engly_server.exception.TokenGenerationException;
import com.engly.engly_server.security.jwt.validation.CompositeTokenValidator;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import com.engly.engly_server.service.common.UserService;
import lombok.RequiredArgsConstructor;
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
public class JwtTokenUtils {
    private final UserService userService;
    private final JwtDecoder jwtDecoder;
    private final CompositeTokenValidator tokenValidator;

    public String getUsername(Jwt jwtToken) {
        return jwtToken.getSubject();
    }

    public Jwt decodeToken(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            throw new TokenGenerationException("Failed to decode JWT token", e);
        }
    }

    public UserDetails loadUserDetails(String email) {
        return new UserDetailsImpl(userService.findUserEntityByEmail(email));
    }

    public Collection<GrantedAuthority> extractAuthorities(Jwt jwt, UserDetails userDetails) {
        Collection<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());

        Optional.ofNullable(jwt.getClaim("scope"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(scopeStr -> Arrays.stream(scopeStr.split(" "))
                        .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                        .forEach(authorities::add));

        return authorities;
    }

    public Authentication createAuthentication(String token) {
        Jwt jwt = decodeToken(token);
        String username = getUsername(jwt);
        UserDetails userDetails = loadUserDetails(username);

        if (!tokenValidator.validateToken(jwt, userDetails))
            throw new TokenGenerationException("Invalid JWT token");

        Collection<GrantedAuthority> authorities = extractAuthorities(jwt, userDetails);
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
}
