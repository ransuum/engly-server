package com.engly.engly_server.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

@FunctionalInterface
public interface JwtHelper {
    UserDetails createUserDetailsFromJwtClaims(Jwt jwt);
}
