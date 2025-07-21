package com.engly.engly_server.models.enums;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

@Getter
public enum RoomRoles {
    ADMIN(EnumSet.of(
            RoomAuthority.ROOM_READ,
            RoomAuthority.ROOM_WRITE,
            RoomAuthority.ROOM_DELETE,
            RoomAuthority.ROOM_MANAGE_USERS,
            RoomAuthority.ROOM_MANAGE_SETTINGS
    )),
    MANAGER(EnumSet.of(
            RoomAuthority.ROOM_READ,
            RoomAuthority.ROOM_WRITE,
            RoomAuthority.ROOM_DELETE,
            RoomAuthority.ROOM_MANAGE_USERS
    )),
    USER(EnumSet.of(
            RoomAuthority.ROOM_READ,
            RoomAuthority.ROOM_WRITE
    )),
    GUEST(EnumSet.of(
            RoomAuthority.ROOM_READ
    )),
    BOT(EnumSet.of(
            RoomAuthority.ROOM_READ,
            RoomAuthority.ROOM_WRITE
    )),
    BANNED(EnumSet.noneOf(RoomAuthority.class));

    private final Set<RoomAuthority> permissions;

    RoomRoles(Set<RoomAuthority> permissions) {
        this.permissions = permissions;
    }
}

