package com.engly.engly_server.models.dto.response;

import com.engly.engly_server.models.enums.ModAction;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public record ModerationDto(String id,
                            RoomsDto room,
                            UsersDto moder,
                            UsersDto user,
                            ModAction action,
                            @Nullable String reason,
                            Instant createdAt) {
}
