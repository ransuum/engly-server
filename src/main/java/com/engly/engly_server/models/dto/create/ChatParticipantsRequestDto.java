package com.engly.engly_server.models.dto.create;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Roles;

public record ChatParticipantsRequestDto(Rooms rooms, Users user, Roles role) {
}
