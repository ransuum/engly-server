package com.engly.engly_server.models.dto.response;

import org.jspecify.annotations.Nullable;

public record CategoriesDto(String id,
                            String name,
                            @Nullable String description,
                            int activeRoomsCount,
                            String icon) {
}
