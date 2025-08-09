package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;
import jakarta.validation.constraints.NotNull;

public record GoogleUserInfoRequest(@NotNull(message = "English level is required")
                                                EnglishLevels englishLevel,

                                    @NotNull(message = "Native language is required")
                                                NativeLanguage nativeLanguage,

                                    @NotNull(message = "Goals are required")
                                                Goals goals,
                                    String imgUrl) {
}
