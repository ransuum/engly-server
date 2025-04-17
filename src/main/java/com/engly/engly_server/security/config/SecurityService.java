package com.engly.engly_server.security.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityService {
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("No authenticated user found");
        }

        return authentication.getName();
    }

    public Collection<SimpleGrantedAuthority> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            throw new AuthenticationCredentialsNotFoundException("No authenticated user found");

        return authentication.getAuthorities()
                .stream()
                .filter(SimpleGrantedAuthority.class::isInstance)
                .map(SimpleGrantedAuthority.class::cast)
                .toList();
    }

    public boolean hasRole(String role) {
        return getCurrentUserRoles().stream().anyMatch(authority
                -> authority.getAuthority().equals(role));
    }
}
