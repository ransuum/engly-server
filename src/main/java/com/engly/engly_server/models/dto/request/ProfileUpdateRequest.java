package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;

public record ProfileUpdateRequest(EnglishLevels englishLevel,
                                   NativeLanguage nativeLanguage,
                                   Goals goal,
                                   String username) {
}
