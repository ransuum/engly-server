package com.engly.engly_server.security.jwt.validation;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

public sealed interface TokenValidation permits ExpirationValidation, SubjectValidation {
    boolean validate(@NonNull Jwt token, @NonNull UserDetails userDetails);
}
