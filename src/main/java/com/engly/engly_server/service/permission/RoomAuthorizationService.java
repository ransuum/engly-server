package com.engly.engly_server.service.permission;

import com.engly.engly_server.models.enums.RoomAuthority;
import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.repository.ChatParticipantRepository;
import com.engly.engly_server.security.config.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@NullMarked
public class RoomAuthorizationService {
    private final ChatParticipantRepository chatParticipantsRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public boolean hasRoomPermission(String roomId, RoomAuthority authority) {
        var userEmail = authenticatedUserProvider.getCurrentUserEmail();
        return hasRoomPermission(userEmail, roomId, authority);
    }

    public boolean hasRoomPermission(String email, String roomId, RoomAuthority authority) {
        try {
            var auth = authenticatedUserProvider.getAuthenticationOrThrow();
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
        var userEmail = authenticatedUserProvider.getCurrentUserEmail();
        return hasRoomRole(userEmail, roomId, requiredRole);
    }

    public boolean hasRoomRole(String userEmail, String roomId, RoomRoles requiredRole) {
        try {
            return chatParticipantsRepository.findByEmailAndRoom(userEmail, roomId)
                    .map(participation -> {
                        var userRole = participation.getRole();
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
                        Objects.equals(authority.getAuthority(), "ROLE_ADMIN") ||
                                Objects.equals(authority.getAuthority(), "ROLE_SYSADMIN"));
    }

    private boolean isHigherRole(RoomRoles userRole, RoomRoles requiredRole) {
        var roleHierarchy = Map.of(
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
        var auth = authenticatedUserProvider.getAuthenticationOrThrow();

        if (hasGlobalAdminRights(auth)) return true;

        var participation = chatParticipantsRepository.findByEmailAndRoom(auth.getName(), roomId);

        return participation.isPresent() && participation.get().getRole() != RoomRoles.BANNED;
    }
}
