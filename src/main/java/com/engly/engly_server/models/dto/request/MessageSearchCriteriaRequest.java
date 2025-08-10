package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.specs.MessageSpecification;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Stream;

@Builder
public record MessageSearchCriteriaRequest(
        String roomId,
        String roomName,
        CategoryType roomCategory,
        String userId,
        String userName,
        String content,
        String keyword,
        Boolean hasImage,
        Boolean isEdited,
        Boolean isDeleted,
        LocalDate createdAfter,
        LocalDate createdBefore,
        LocalDate createdStartDate,
        LocalDate createdEndDate,
        LocalDate updatedAfter,
        LocalDate updatedBefore,
        LocalDate updatedStartDate,
        LocalDate updatedEndDate,
        Integer minReads,
        Integer maxReads,
        String readByUserId,
        String unreadByUserId,
        LocalDate readAfter,
        LocalDate readBefore
) {

    public Specification<Message> buildSpecification() {
        return Stream.of(
                        MessageSpecification.hasRoom(this.roomId()),
                        MessageSpecification.hasRoomName(this.roomName()),
                        MessageSpecification.hasRoomCategory(this.roomCategory()),
                        MessageSpecification.hasUser(this.userId()),
                        MessageSpecification.hasUserName(this.userName()),
                        MessageSpecification.hasContentContaining(this.content()),
                        MessageSpecification.hasKeywordInContent(this.keyword()),
                        buildImageSpecification(),
                        MessageSpecification.isEdited(this.isEdited()),
                        MessageSpecification.isDeleted(this.isDeleted()),
                        MessageSpecification.createdAfter(this.createdAfter()),
                        MessageSpecification.createdBefore(this.createdBefore()),
                        MessageSpecification.createdBetween(this.createdStartDate(), this.createdEndDate()),
                        MessageSpecification.updatedAfter(this.updatedAfter()),
                        MessageSpecification.updatedBefore(this.updatedBefore()),
                        MessageSpecification.updatedBetween(this.updatedStartDate(), this.updatedEndDate()),
                        MessageSpecification.hasMinimumReads(this.minReads()),
                        MessageSpecification.hasMaximumReads(this.maxReads()),
                        MessageSpecification.isReadByUserExists(this.readByUserId()),
                        MessageSpecification.isUnreadByUserExists(this.unreadByUserId()),
                        MessageSpecification.isReadByUserAfter(this.readByUserId(), this.readAfter()),
                        MessageSpecification.isReadByUserBefore(this.readByUserId(), this.readBefore())
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
