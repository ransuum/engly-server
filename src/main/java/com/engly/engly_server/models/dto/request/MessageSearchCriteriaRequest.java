package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.specs.MessageSpecification;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Stream;

@Builder
public record MessageSearchCriteriaRequest(
        String roomId,
        String roomName,
        CategoryType roomCategory,
        String userId,
        String username,
        String content,
        String keyword,
        Boolean hasImage,
        Boolean isEdited,
        Boolean isDeleted,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAfter,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdBefore,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdStartDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdEndDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedAfter,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedBefore,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedStartDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedEndDate,

        Integer minReads,
        Integer maxReads,
        String readByUserId,
        String unreadByUserId,
        String readByUsername,
        String unreadByUsername,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate readAfter,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate readBefore
) {

    public Specification<Message> buildSpecification() {
        return Stream.of(
                        MessageSpecification.hasRoom(this.roomId),
                        MessageSpecification.hasRoomName(this.roomName),
                        MessageSpecification.hasRoomCategory(this.roomCategory),
                        MessageSpecification.hasUser(this.userId),
                        MessageSpecification.hasUsername(this.username),
                        MessageSpecification.hasContentContaining(this.content),
                        MessageSpecification.hasKeywordInContent(this.keyword),
                        buildImageSpecification(),
                        MessageSpecification.isEdited(this.isEdited),
                        MessageSpecification.isDeleted(this.isDeleted),
                        MessageSpecification.createdAfter(this.createdAfter),
                        MessageSpecification.createdBefore(this.createdBefore),
                        MessageSpecification.createdBetween(this.createdStartDate, this.createdEndDate),
                        MessageSpecification.updatedAfter(this.updatedAfter),
                        MessageSpecification.updatedBefore(this.updatedBefore),
                        MessageSpecification.updatedBetween(this.updatedStartDate, this.updatedEndDate),
                        MessageSpecification.hasMinimumReads(this.minReads),
                        MessageSpecification.hasMaximumReads(this.maxReads),
                        MessageSpecification.isReadByUsernameExists(this.readByUsername),
                        MessageSpecification.isUnreadByUsernameExists(this.unreadByUsername),
                        MessageSpecification.isReadByUserExists(this.readByUserId),
                        MessageSpecification.isUnreadByUserExists(this.unreadByUserId),
                        this.readByUsername != null
                                ? MessageSpecification.isReadByUsernameAfter(this.readByUsername, this.readAfter)
                                : MessageSpecification.isReadByUserAfter(this.readByUserId, this.readAfter),
                        this.readByUsername != null
                                ? MessageSpecification.isReadByUsernameBefore(this.readByUsername, this.readBefore)
                                : MessageSpecification.isReadByUserBefore(this.readByUserId, this.readBefore)
                )
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);
    }

    private Specification<Message> buildImageSpecification() {
        if (this.hasImage() == null) return null;

        return this.hasImage()
                ? MessageSpecification.hasImageUrl()
                : MessageSpecification.hasNoImageUrl();
    }
}
