package com.engly.engly_server.models.enums;

import lombok.Getter;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum Roles {
    ROLE_ADMIN(Set.of("READ", "WRITE", "DELETE", "CREATE_GLOBAL", "UPDATE_GLOBAL", "DELETE_GLOBAL", "ADMIN", "AUTHORIZE")),
    ROLE_MANAGER(Set.of("READ", "WRITE", "DELETE", "AUTHORIZE")),
    ROLE_USER(Set.of("READ", "WRITE", "AUTHORIZE")),
    ROLE_GOOGLE(Set.of("ADDITIONAL_INFO", "AUTHORIZE")),
    ROLE_SYSADMIN(Set.of("READ", "WRITE", "DELETE", "CREATE_GLOBAL", "UPDATE_GLOBAL", "DELETE_GLOBAL", "AUTHORIZE")),
    ROLE_NOT_VERIFIED(Set.of("NOT_VERIFIED", "AUTHORIZE"));

    private final Set<String> permissions;

    Roles(Set<String> permissions) {
        this.permissions = permissions;
    }

    public static Set<String> getPermissionsForRoles(Collection<String> roles) {
        return roles.stream().map(Roles::valueOf)
                .flatMap(applicationRole -> applicationRole.getPermissions().stream())
                .collect(Collectors.toSet());
    }
}
