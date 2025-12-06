package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;
import org.jspecify.annotations.Nullable;

public record ProfileUpdateRequest(@Nullable EnglishLevels englishLevel,
                                   @Nullable NativeLanguage nativeLanguage,
                                   @Nullable Goals goal,
                                   @Nullable String username) {
}
