package com.engly.engly_server.security.jwt;

import com.engly.engly_server.exception.TokenNotFoundException;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
public class JwtTokenUtils {
    private final UserRepo userRepo;
    private final JwtDecoder jwtDecoder;

    public JwtTokenUtils(UserRepo userRepo, @Lazy JwtDecoder jwtDecoder) {
        this.userRepo = userRepo;
        this.jwtDecoder = jwtDecoder;
    }

    public String getUserName(Jwt jwtToken) {
        return jwtToken.getSubject();
    }

    public boolean isTokenValid(Jwt jwtToken, UserDetails userDetails) {
        if (getIfTokenIsExpired(jwtToken)) return false;
        return getUserName(jwtToken).equals(userDetails.getUsername());
    }

    private boolean getIfTokenIsExpired(Jwt jwtToken) {
        return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }

    public UserDetails userDetails(String email) {
        return userRepo.findByEmail(email)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Your account can be banned or deleted from the chat"));
    }

    public Authentication getAuthentication(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        String username = getUserName(jwt);
        UserDetails userDetails = userDetails(username);
        if (!isTokenValid(jwt, userDetails)) throw new TokenNotFoundException("Invalid JWT token");
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }
}
