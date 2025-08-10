package com.engly.engly_server.specs;

import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.entity.MessageRead;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Objects;

public class MessageSpecification {

    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String UPDATED_AT_FIELD = "updatedAt";
    private static final String READ_AT_FIELD = "readAt";
    private static final String MESSAGE_ID_FIELD = "messageId";
    private static final String USER_ID_FIELD = "userId";
    private static final String ID_FIELD = "id";
    private static final String CONTENT_FIELD = "content";
    private static final String IMAGE_URL_FIELD = "imageUrl";
    private static final String IS_EDITED_FIELD = "isEdited";
    private static final String IS_DELETED_FIELD = "isDeleted";
    private static final String MESSAGE_READS_FIELD = "messageReads";

    private static final String ROOM_ID_PATH = "room.id";
    private static final String ROOM_NAME_PATH = "room.name";
    private static final String ROOM_CATEGORY_NAME_PATH = "room.category.name";
    private static final String USER_ID_PATH = "user.id";
    private static final String USER_NAME_PATH = "user.name";

    private MessageSpecification() {}

    public static Specification<Message> hasRoom(String roomId) {
        return createEqualSpecification(ROOM_ID_PATH, roomId);
    }

    public static Specification<Message> hasRoomName(String roomName) {
        return createLikeSpecification(ROOM_NAME_PATH, roomName);
    }

    public static Specification<Message> hasRoomCategory(CategoryType categoryType) {
        return createEqualSpecification(ROOM_CATEGORY_NAME_PATH, categoryType);
    }

    public static Specification<Message> hasUser(String userId) {
        return createEqualSpecification(USER_ID_PATH, userId);
    }

    public static Specification<Message> hasUserName(String userName) {
        return createLikeSpecification(USER_NAME_PATH, userName);
    }

    public static Specification<Message> hasContentContaining(String content) {
        return createLikeSpecification(CONTENT_FIELD, content);
    }

    public static Specification<Message> hasKeywordInContent(String keyword) {
        return createLikeSpecification(CONTENT_FIELD, keyword);
    }

    public static Specification<Message> hasImageUrl() {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get(IMAGE_URL_FIELD));
    }

    public static Specification<Message> hasNoImageUrl() {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get(IMAGE_URL_FIELD));
    }

    public static Specification<Message> isEdited(Boolean isEdited) {
        return createEqualSpecification(IS_EDITED_FIELD, isEdited);
    }

    public static Specification<Message> isDeleted(Boolean isDeleted) {
        return createEqualSpecification(IS_DELETED_FIELD, isDeleted);
    }

    public static Specification<Message> isNotDeleted() {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.isNull(root.get(IS_DELETED_FIELD)),
                        criteriaBuilder.equal(root.get(IS_DELETED_FIELD), false)
                );
    }

    public static Specification<Message> createdAfter(LocalDate date) {
        return createDateAfterSpecification(CREATED_AT_FIELD, date);
    }

    public static Specification<Message> createdBefore(LocalDate date) {
        return createDateBeforeSpecification(CREATED_AT_FIELD, date);
    }

    public static Specification<Message> createdBetween(LocalDate startDate, LocalDate endDate) {
        return createDateBetweenSpecification(CREATED_AT_FIELD, startDate, endDate);
    }

    public static Specification<Message> updatedAfter(LocalDate date) {
        return createDateAfterSpecification(UPDATED_AT_FIELD, date);
    }

    public static Specification<Message> updatedBefore(LocalDate date) {
        return createDateBeforeSpecification(UPDATED_AT_FIELD, date);
    }

    public static Specification<Message> updatedBetween(LocalDate startDate, LocalDate endDate) {
        return createDateBetweenSpecification(UPDATED_AT_FIELD, startDate, endDate);
    }

    public static Specification<Message> hasMinimumReads(Integer minReads) {
        return createSizeComparisonSpecification(minReads, true);
    }

    public static Specification<Message> hasMaximumReads(Integer maxReads) {
        return createSizeComparisonSpecification(maxReads, false);
    }

    public static Specification<Message> isReadByUser(String userId) {
        return createUserReadStatusSpecification(userId, false, false);
    }

    public static Specification<Message> isUnreadByUser(String userId) {
        return createUserReadStatusSpecification(userId, true, false);
    }

    public static Specification<Message> isReadByUserExists(String userId) {
        return createUserReadStatusSpecification(userId, false, true);
    }

    public static Specification<Message> isUnreadByUserExists(String userId) {
        return createUserReadStatusSpecification(userId, true, true);
    }

    public static Specification<Message> isReadByUserAfter(String userId, LocalDate readAfter) {
        return createUserReadWithDateSpecification(userId, readAfter, true);
    }

    public static Specification<Message> isReadByUserBefore(String userId, LocalDate readBefore) {
        return createUserReadWithDateSpecification(userId, readBefore, false);
    }

    public static Specification<Message> isReadByUserJoin(String userId) {
        return (root, _, criteriaBuilder) -> {
            if (!FieldUtil.isValid(userId)) return null;

            var messageReadJoin = root.join(MESSAGE_READS_FIELD);
            return criteriaBuilder.equal(messageReadJoin.get(USER_ID_FIELD), userId);
        };
    }

    public static Specification<Message> inRoomWithReadStatus(String roomId) {
        return createEqualSpecification(ROOM_ID_PATH, roomId);
    }

    private static <T> Specification<Message> createEqualSpecification(String fieldPath, T value) {
        return (root, _, criteriaBuilder) -> {
            if (value == null || (value instanceof String str && str.isBlank()))
                return null;

            return criteriaBuilder.equal(getNestedField(root, fieldPath), value);
        };
    }

    private static Specification<Message> createLikeSpecification(String fieldPath, String value) {
        return (root, _, criteriaBuilder) -> {
            if (!FieldUtil.isValid(value)) return null;

            final var lowerValue = "%" + value.toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(getNestedField(root, fieldPath)),
                    lowerValue
            );
        };
    }

    private static Specification<Message> createDateAfterSpecification(String dateField, LocalDate date) {
        return (root, _, criteriaBuilder) -> {
            if (date == null) return null;

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get(dateField),
                    toInstant(date)
            );
        };
    }

    private static Specification<Message> createDateBeforeSpecification(String dateField, LocalDate date) {
        return (root, _, criteriaBuilder) -> {
            if (date == null) return null;

            return criteriaBuilder.lessThan(
                    root.get(dateField),
                    toInstant(date.plusDays(1))
            );
        };
    }

    private static Specification<Message> createDateBetweenSpecification(String dateField, LocalDate startDate, LocalDate endDate) {
        return (root, _, criteriaBuilder) -> {
            if (startDate == null && endDate == null) return null;

            var conditions = new ArrayList<Predicate>();

            if (startDate != null)
                conditions.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get(dateField),
                        toInstant(startDate)
                ));

            if (endDate != null)
                conditions.add(criteriaBuilder.lessThan(
                        root.get(dateField),
                        toInstant(endDate.plusDays(1))
                ));

            return criteriaBuilder.and(conditions.toArray(new Predicate[0]));
        };
    }

    private static Specification<Message> createSizeComparisonSpecification(Integer value, boolean isMinimum) {
        return (root, _, criteriaBuilder) -> {
            if (value == null) return null;

            final var sizeExpression = criteriaBuilder.size(root.get(MessageSpecification.MESSAGE_READS_FIELD));
            return isMinimum
                    ? criteriaBuilder.greaterThanOrEqualTo(sizeExpression, value)
                    : criteriaBuilder.lessThanOrEqualTo(sizeExpression, value);
        };
    }

    private static Specification<Message> createUserReadStatusSpecification(String userId, boolean isUnread, boolean useExists) {
        return (root, query, criteriaBuilder) -> {
            if (!FieldUtil.isValid(userId)) return null;

            if (useExists) return createExistsSubquery(root, query, criteriaBuilder, userId, isUnread);
            else return createInSubquery(root, query, criteriaBuilder, userId, isUnread);
        };
    }

    private static Specification<Message> createUserReadWithDateSpecification(String userId, LocalDate date, boolean isAfter) {
        return (root, query, criteriaBuilder) -> {
            if (!FieldUtil.isValid(userId) || date == null) return null;

            Subquery<String> subquery = Objects.requireNonNull(query).subquery(String.class);
            final var messageReadRoot = subquery.from(MessageRead.class);

            final var dateCondition = isAfter
                    ? criteriaBuilder.greaterThanOrEqualTo(
                    messageReadRoot.get(READ_AT_FIELD),
                    toInstant(date)
            )
                    : criteriaBuilder.lessThan(
                    messageReadRoot.get(READ_AT_FIELD),
                    toInstant(date.plusDays(1))
            );

            subquery.select(messageReadRoot.get(MESSAGE_ID_FIELD))
                    .where(criteriaBuilder.and(
                            criteriaBuilder.equal(messageReadRoot.get(USER_ID_FIELD), userId),
                            dateCondition
                    ));

            return root.get(ID_FIELD).in(subquery);
        };
    }

    private static Predicate createExistsSubquery(Root<Message> root, CriteriaQuery<?> query,
                                                  CriteriaBuilder criteriaBuilder, String userId, boolean isUnread) {
        Subquery<Long> subquery = Objects.requireNonNull(query).subquery(Long.class);
        final var messageReadRoot = subquery.from(MessageRead.class);

        subquery.select(criteriaBuilder.literal(1L))
                .where(criteriaBuilder.and(
                        criteriaBuilder.equal(messageReadRoot.get(MESSAGE_ID_FIELD), root.get(ID_FIELD)),
                        criteriaBuilder.equal(messageReadRoot.get(USER_ID_FIELD), userId)
                ));

        final var existsPredicate = criteriaBuilder.exists(subquery);
        return isUnread ? criteriaBuilder.not(existsPredicate) : existsPredicate;
    }

    private static Predicate createInSubquery(Root<Message> root, CriteriaQuery<?> query,
                                              CriteriaBuilder criteriaBuilder, String userId, boolean isUnread) {
        Subquery<String> subquery = Objects.requireNonNull(query).subquery(String.class);
        final var messageReadRoot = subquery.from(MessageRead.class);

        subquery.select(messageReadRoot.get(MESSAGE_ID_FIELD))
                .where(criteriaBuilder.equal(messageReadRoot.get(USER_ID_FIELD), userId));

        final var inPredicate = root.get(ID_FIELD).in(subquery);
        return isUnread ? criteriaBuilder.not(inPredicate) : inPredicate;
    }

    private static Instant toInstant(LocalDate date) {
        return date.atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    @SuppressWarnings("unchecked")
    private static <T> Path<T> getNestedField(Root<Message> root, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        jakarta.persistence.criteria.Path<?> path = root;

        for (String part : parts) path = path.get(part);

        return (jakarta.persistence.criteria.Path<T>) path;
    }
}