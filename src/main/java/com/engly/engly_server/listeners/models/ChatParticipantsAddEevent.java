package com.engly.engly_server.listeners.models;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Roles;
import lombok.NonNull;

public record ChatParticipantsAddEevent(@NonNull Rooms rooms, @NonNull Users user, @NonNull Roles role) { }
