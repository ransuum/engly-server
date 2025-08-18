package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.response.UserSettingsDto;
import com.engly.engly_server.models.enums.Theme;

public interface UserSettingService {
    UserSettingsDto getById(String id);

    void update(String id, Boolean notifications, Theme theme);
}
