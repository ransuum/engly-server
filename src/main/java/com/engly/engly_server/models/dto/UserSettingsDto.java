package com.engly.engly_server.models.dto;

import com.engly.engly_server.models.enums.NativeLanguage;
import com.engly.engly_server.models.enums.Theme;

public record UserSettingsDto(String id,
                              Theme theme,
                              boolean notifications,
                              NativeLanguage interfaceLanguage) {
}
