package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.specs.MessageSpecification;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record MessageSearchCriteriaRequest(
        String roomId,
        String roomName,
        CategoryType roomCategory,
        String userId,
        String username,
        String content,
        String keyword,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAfter,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdBefore,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdStartDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdEndDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedAfter,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedBefore
) {

    public Specification<Message> buildSpecification() {
        return Specification.anyOf(
                MessageSpecification.between(createdStartDate, createdEndDate),
                MessageSpecification.contentLike(content),
                MessageSpecification.createdAfter(createdAfter),
                MessageSpecification.createdBefore(createdBefore),
                MessageSpecification.usernameLike(username),
                MessageSpecification.updatedAfter(updatedAfter),
                MessageSpecification.updatedBefore(updatedBefore),
                MessageSpecification.userIdEquals(userId),
                MessageSpecification.roomIdEquals(roomId),
                MessageSpecification.roomNameLike(roomName),
                MessageSpecification.search(keyword)
        );
    }
}
