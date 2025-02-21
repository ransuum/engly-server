package com.engly.engly_server.models.dto;

import com.engly.engly_server.models.enums.CategoryType;

import java.time.Instant;
import java.util.List;

public record CategoriesDto(String id,
                            CategoryType name,
                            String description,
                            Instant createdAt,
                            Instant updatedAt,
                            List<RoomsDto> rooms) {
}
