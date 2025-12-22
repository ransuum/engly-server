package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public record CategoryRequest(@NotNull(message = "Name is required")
                              CategoryType name,

                              @NotBlank(message = "Description is blank")
                              @Size(min = 3, max = 200, message = "Description must be between 3 and 200 characters")
                              @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-_.,!?()]+$",
                                      message = "Description contains invalid characters")
                              String description) {
}
