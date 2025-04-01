package com.engly.engly_server.models.request.create;

import com.engly.engly_server.models.enums.CategoryType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(@NotNull(message = "Name is required") @Nullable CategoryType name,
                              @NotBlank(message = "Description is blank") @Nullable String description) {
}
