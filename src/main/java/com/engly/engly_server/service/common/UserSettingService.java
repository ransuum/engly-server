package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.UserSettingsDto;
import com.engly.engly_server.models.enums.Theme;

public interface UserSettingService {
    void create(Boolean notifications, Theme theme);

    UserSettingsDto getById();

    void update(Boolean notifications, Theme theme);
}
