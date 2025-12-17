package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.UserSettingsDto;
import com.engly.engly_server.models.entity.UserSettings;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;

@Mapper(uses = UserMapper.class, componentModel = "spring")
public interface UserSettingsMapper {
    @NonNull UserSettingsDto toUserSettingsDto(@NonNull UserSettings userSettings);
}
