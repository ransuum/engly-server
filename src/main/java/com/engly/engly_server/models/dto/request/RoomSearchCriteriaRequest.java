package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.specs.RoomSpecification;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.stream.Stream;

@Builder
public record RoomSearchCriteriaRequest(
        CategoryType categoryType,
        String keyword,
        String name,
        String description,
        String creatorId,
        String creatorName,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAfter,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdBefore,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdStartDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdEndDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedAfter,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedBefore,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedStartDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedEndDate,

        Integer minParticipants,
        Integer maxParticipants
) {

    public Specification<Rooms> buildSpecification() {
        return Stream.of(
                        RoomSpecification.hasCategory(this.categoryType()),
                        RoomSpecification.hasKeywordInNameOrDescription(this.keyword()),
                        RoomSpecification.hasNameContaining(this.name()),
                        RoomSpecification.hasDescriptionContaining(this.description()),
                        RoomSpecification.hasCreator(this.creatorId()),
                        RoomSpecification.hasCreatorName(this.creatorName()),
                        RoomSpecification.createdAfter(this.createdAfter()),
                        RoomSpecification.createdBefore(this.createdBefore()),
                        RoomSpecification.createdBetween(this.createdStartDate(), this.createdEndDate()),
                        RoomSpecification.updatedAfter(this.updatedAfter()),
                        RoomSpecification.updatedBefore(this.updatedBefore()),
                        RoomSpecification.updatedBetween(this.updatedStartDate(), this.updatedEndDate()),
                        RoomSpecification.hasMinimumParticipants(this.minParticipants()),
                        RoomSpecification.hasMaximumParticipants(this.maxParticipants())
                )
                .reduce(Specification::and)
                .orElse(null);
    }
}
