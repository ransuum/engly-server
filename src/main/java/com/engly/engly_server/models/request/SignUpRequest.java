package com.engly.engly_server.models.request;

import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Gender;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record SignUpRequest(@NotBlank(message = "Username is blank")
                            @Size(max = 18, message = "Username is too long")
                            String username,

                            @Valid
                            @Email(message = "Isn't email")
                            @NotBlank(message = "Email is blank")
                            @Size(max = 40, message = "Email is too long")
                            String email,

                            @Valid
                            @NotBlank(message = "Password is blank")
                            @Size(min = 8, max = 30, message = "Password size should be from 9 to 30 characters")
                            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*#?&_-]+$",
                                    message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
                            String password,
                            @NotNull(message = "Date of birth is required")
                            LocalDate dateOfBirth,

                            @NotNull(message = "English level is required")
                            EnglishLevels englishLevel,

                            @NotNull(message = "Native language is required")
                            NativeLanguage nativeLanguage,

                            @NotNull(message = "Goals are required")
                            Goals goals,

                            @NotNull(message = "Gender is required")
                            Gender gender,

                            String providerId) {
}
