package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.specs.RoomSpecification;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record RoomSearchCriteriaRequest(
        String keyword,
        String name,
        String description,
        String categoryId,
        String creatorUsername,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAfter,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdBefore,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

        Integer minParticipants,
        Integer maxParticipants
) {

    public Specification<Rooms> buildSpecification() {
        return Specification.allOf(
                RoomSpecification.search(keyword),
                RoomSpecification.nameLike(name),
                RoomSpecification.search(description),
                RoomSpecification.categoryEquals(categoryId),
                RoomSpecification.creatorUsernameLike(creatorUsername),
                RoomSpecification.createdAfter(createdAfter),
                RoomSpecification.createdBefore(createdBefore),
                RoomSpecification.descriptionLike(description),
                RoomSpecification.between(startDate, endDate)
        );
    }
}
