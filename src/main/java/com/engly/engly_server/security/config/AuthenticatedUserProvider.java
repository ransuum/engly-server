package com.engly.engly_server.security.config;

import com.engly.engly_server.exception.AuthenticationObjectException;
import com.engly.engly_server.models.enums.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.engly.engly_server.exception.handler.ExceptionMessage.AUTHENTICATION_OBJECT_NOT_FOUND;
import static com.engly.engly_server.exception.handler.ExceptionMessage.NO_AUTHENTICATED_USER_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@NullMarked
public class AuthenticatedUserProvider {

    public String getCurrentUserEmail() {
        return getAuthenticationOrThrow()
                .orElseThrow(() -> new AuthenticationObjectException(AUTHENTICATION_OBJECT_NOT_FOUND))
                .getName();
    }

    public boolean hasRole(String role) {
        return getCurrentUserRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    public String getRolesOfUser(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    public String getPermissionsFromRoles(String roles) {
        var roleList = Arrays.stream(roles.split(","))
                .map(String::trim)
                .toList();

        var authorities = Roles.getPermissionsForRoles(roleList);
        return authorities.stream()
                .map(Enum::name)
                .collect(Collectors.joining(" "));
    }

    private Collection<SimpleGrantedAuthority> getCurrentUserRoles() {
        var authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        return authentication.map(authenticationSafe ->
                        authenticationSafe.getAuthorities()
                                .stream()
                                .filter(SimpleGrantedAuthority.class::isInstance)
                                .map(SimpleGrantedAuthority.class::cast)
                                .toList())
                .orElseThrow(() -> new AuthenticationObjectException(NO_AUTHENTICATED_USER_FOUND));
    }

    public Optional<Authentication> getAuthenticationOrThrow() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

}
