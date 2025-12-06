package com.engly.engly_server.models.dto.response;

import com.engly.engly_server.models.enums.Provider;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public record UsersDto(String id,
                       String username,
                       String email,
                       @Nullable String providerId,
                       Instant createdAt,
                       String roles,
                       @Nullable String imgUrl,
                       Boolean emailVerified,
                       Instant updatedAt,
                       Instant lastLogin,
                       Provider provider,
                       AdditionalInfoDto additionalInfo,
                       UserSettingsDto userSettings) {
}
