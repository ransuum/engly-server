package com.engly.engly_server.security.jwt.validation;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

public sealed interface TokenValidation permits ExpirationValidation, SubjectValidation {
    boolean validate(Jwt token, UserDetails userDetails);
}
