package com.engly.engly_server.models.dto.response;

public record CategoriesDto(String id,
                            String name,
                            String description,
                            int activeRoomsCount,
                            String icon) {
}
