package com.engly.engly_server.security.jwt.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CompositeTokenValidator {
    private final List<TokenValidation> validationStrategies;

    public boolean validateToken(Jwt token, UserDetails userDetails) {
        return validationStrategies.stream()
                .allMatch(strategy -> strategy.validate(token, userDetails));
    }
}
