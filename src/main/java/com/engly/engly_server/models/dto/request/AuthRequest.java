package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthRequest.SignUpRequest.class, name = "signup"),
        @JsonSubTypes.Type(value = AuthRequest.SignInRequest.class, name = "signin")
})
public sealed interface     AuthRequest {

    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    record SignUpRequest(
            @NotBlank(message = "Username is blank")
            @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters.")
            @Pattern(
                    regexp = "^[a-zA-Z]{2,50}$",
                    message = "Username must contain only letters (a-z, A-Z) and be between 2 and 50 characters long."
            )
            String username,

            @Valid
            @Email(message = "Isn't email")
            @NotBlank(message = "Email is blank")
            @Size(max = 50, message = "Email cannot exceed 50 characters. Please shorten your input.")
            String email,

            @Valid
            @NotBlank(message = "Password is blank")
            @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*#?&_-]+$",
                    message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
            String password,

            @NotNull(message = "English level is required")
            EnglishLevels englishLevel,

            @NotNull(message = "Native language is required")
            NativeLanguage nativeLanguage,

            @NotNull(message = "Goals are required")
            Goals goals,

            String imgUrl,

            @JsonIgnore
            String providerId
    ) implements AuthRequest { }

    record SignInRequest(
            @Valid
            @Email(message = "Isn't email")
            @NotBlank(message = "Email is blank")
            @Size(max = 50, message = "Email cannot exceed 50 characters. Please shorten your input.")
            String email,

            @Valid
            @NotBlank(message = "Password is blank")
            @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*#?&_-]+$",
                    message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
            String password
    ) implements AuthRequest { }
}
