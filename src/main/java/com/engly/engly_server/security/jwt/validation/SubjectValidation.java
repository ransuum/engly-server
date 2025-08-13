package com.engly.engly_server.security.jwt.validation;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class SubjectValidation implements TokenValidation {

    @Override
    public boolean validate(Jwt token, UserDetails userDetails) {
        return token.getSubject().equals(Objects.requireNonNull(userDetails).getUsername());
    }
}
