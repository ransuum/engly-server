package com.engly.engly_server.service.permission;

import com.engly.engly_server.exception.AuthenticationObjectException;
import com.engly.engly_server.models.enums.RoomAuthority;
import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.repository.ChatParticipantRepository;
import com.engly.engly_server.security.config.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomAuthorizationService {
    private final ChatParticipantRepository chatParticipantsRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    private static final String AUTH_NOT_FOUND = "Authentication object not found";

    public boolean hasRoomPermission(String roomId, RoomAuthority authority) {
        final var userEmail = authenticatedUserProvider.getCurrentUserEmail();
        return hasRoomPermission(userEmail, roomId, authority);
    }

    public boolean hasRoomPermission(String email, String roomId, RoomAuthority authority) {
        try {
            final var auth = authenticatedUserProvider.getAuthenticationOrThrow()
                    .orElseThrow(() -> new AuthenticationObjectException(AUTH_NOT_FOUND));
            if (hasGlobalAdminRights(auth)) return true;

            return chatParticipantsRepository.findByEmailAndRoom(email, roomId)
                    .map(chatParticipants -> {
                        RoomRoles roomRole = chatParticipants.getRole();
                        return roomRole.getPermissions().contains(authority);
                    }).orElse(false);
        } catch (Exception e) {
            log.error("Error checking room permission for user {} in room {}: {}", email, roomId, e.getMessage());
            return false;
        }
    }

    public boolean hasRoomRole(String roomId, RoomRoles requiredRole) {
        final var userEmail = authenticatedUserProvider.getCurrentUserEmail();
        return hasRoomRole(userEmail, roomId, requiredRole);
    }

    public boolean hasRoomRole(String userEmail, String roomId, RoomRoles requiredRole) {
        try {
            return chatParticipantsRepository.findByEmailAndRoom(userEmail, roomId)
                    .map(participation -> {
                        final var userRole = participation.getRole();
                        return userRole == requiredRole || isHigherRole(userRole, requiredRole);
                    })
                    .orElse(false);
        } catch (Exception e) {
            log.error("Error checking room role for user {} in room {}: {}", userEmail, roomId, e.getMessage());
            return false;
        }
    }

    private boolean hasGlobalAdminRights(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN") ||
                                authority.getAuthority().equals("ROLE_SYSADMIN"));
    }

    private boolean isHigherRole(RoomRoles userRole, RoomRoles requiredRole) {
        final var roleHierarchy = Map.of(
                RoomRoles.BANNED, 0,
                RoomRoles.GUEST, 1,
                RoomRoles.USER, 2,
                RoomRoles.BOT, 2,
                RoomRoles.MANAGER, 3,
                RoomRoles.ADMIN, 4
        );

        return roleHierarchy.getOrDefault(userRole, 0) >= roleHierarchy.getOrDefault(requiredRole, 0);
    }

    public boolean canAccessRoom(String roomId) {
        final var auth = authenticatedUserProvider.getAuthenticationOrThrow()
                .orElseThrow(() -> new AuthenticationObjectException(AUTH_NOT_FOUND));

        if (hasGlobalAdminRights(auth)) return true;

        final var participation = chatParticipantsRepository
                .findByEmailAndRoom(auth.getName(), roomId);

        return participation.isPresent() && participation.get().getRole() != RoomRoles.BANNED;
    }
}
