package com.engly.engly_server.security.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityService {
    public String getCurrentUserEmail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            throw new AuthenticationCredentialsNotFoundException("No authenticated user found");

        return authentication.getName();
    }

    public Collection<SimpleGrantedAuthority> getCurrentUserRoles() {
        final Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        return authentication.map(authentication1 ->
                        authentication1.getAuthorities()
                                .stream()
                                .filter(SimpleGrantedAuthority.class::isInstance)
                                .map(SimpleGrantedAuthority.class::cast)
                                .toList())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("No authenticated user found"));
    }

    public boolean hasAnyRole(List<String> roles) {
        return getCurrentUserRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roles::contains);
    }

    public boolean hasRole(String role) {
        return getCurrentUserRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}
