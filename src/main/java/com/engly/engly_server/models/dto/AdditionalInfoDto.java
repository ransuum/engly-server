package com.engly.engly_server.models.dto;

import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Gender;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;

import java.time.LocalDate;

public record AdditionalInfoDto(String id,
                                EnglishLevels englishLevel,
                                NativeLanguage nativeLanguage,
                                Goals goal) {
}
