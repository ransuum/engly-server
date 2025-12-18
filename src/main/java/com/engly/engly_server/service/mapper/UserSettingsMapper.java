package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.UserSettingsDto;
import com.engly.engly_server.models.entity.UserSettings;
import org.mapstruct.Mapper;

@Mapper(uses = UserMapper.class, componentModel = "spring")
public interface UserSettingsMapper {
    UserSettingsDto toUserSettingsDto(UserSettings userSettings);
}
