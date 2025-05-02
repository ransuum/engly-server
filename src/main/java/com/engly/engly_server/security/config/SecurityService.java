package com.engly.engly_server.security.config;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class SecurityService {

    @Transactional(readOnly = true)
    public String getCurrentUserEmail() {
        return getAuthenticationOrThrow().getName();
    }

    @Transactional(readOnly = true)
    public boolean hasAnyRole(List<String> roles) {
        return getCurrentUserRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roles::contains);
    }

    @Transactional(readOnly = true)
    public boolean hasRole(String role) {
        return getCurrentUserRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    private Collection<SimpleGrantedAuthority> getCurrentUserRoles() {
        final Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        return authentication.map(authentication1 ->
                        authentication1.getAuthorities()
                                .stream()
                                .filter(SimpleGrantedAuthority.class::isInstance)
                                .map(SimpleGrantedAuthority.class::cast)
                                .toList())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("No authenticated user found"));
    }

    private Authentication getAuthenticationOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            throw new AuthenticationCredentialsNotFoundException("No authenticated user found in SecurityContext");

        return authentication;
    }
}
