package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.UserSettingsDto;
import com.engly.engly_server.models.entity.UserSettings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UserMapper.class)
public interface UserSettingsMapper {
    UserSettingsMapper INSTANCE = Mappers.getMapper(UserSettingsMapper.class);

    UserSettingsDto toUserSettingsDto(UserSettings userSettings);
}
