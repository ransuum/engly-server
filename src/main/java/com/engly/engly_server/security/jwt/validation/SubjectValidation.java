package com.engly.engly_server.security.jwt.validation;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public final class SubjectValidation implements TokenValidation {

    @Override
    public boolean validate(Jwt token, UserDetails userDetails) {
        return token.getSubject().equals(userDetails.getUsername());
    }
}
