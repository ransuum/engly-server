package com.engly.engly_server.models.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MessagePageResponse(
        List<MessagesDto> messages,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages,
        int numberOfElements,
        boolean hasNext,
        boolean hasPrevious,
        boolean isFirst,
        boolean isLast
) {
}
