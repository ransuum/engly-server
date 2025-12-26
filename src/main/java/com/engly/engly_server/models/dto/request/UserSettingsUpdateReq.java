package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.enums.NativeLanguage;
import com.engly.engly_server.models.enums.Theme;
import org.jspecify.annotations.Nullable;

public record UserSettingsUpdateReq(@Nullable NativeLanguage nativeLanguage,
                                    @Nullable Boolean notifications,
                                    @Nullable Theme theme) { }
