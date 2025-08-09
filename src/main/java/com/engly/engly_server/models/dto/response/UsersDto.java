package com.engly.engly_server.models.dto.response;

import com.engly.engly_server.models.enums.Provider;

import java.time.Instant;

public record UsersDto(String id,
                       String username,
                       String email,
                       String providerId,
                       Instant createdAt,
                       String roles,
                       String imgUrl,
                       Boolean emailVerified,
                       Instant updatedAt,
                       Instant lastLogin,
                       Provider provider,
                       AdditionalInfoDto additionalInfo,
                       UserSettingsDto userSettings) {
}
