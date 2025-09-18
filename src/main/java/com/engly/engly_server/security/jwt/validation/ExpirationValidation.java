package com.engly.engly_server.security.jwt.validation;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
public final class ExpirationValidation implements TokenValidation {

    @Override
    public boolean validate(@NonNull Jwt token, @NonNull UserDetails userDetails) {
        return !Objects.requireNonNull(token.getExpiresAt()).isBefore(Instant.now());
    }
}
