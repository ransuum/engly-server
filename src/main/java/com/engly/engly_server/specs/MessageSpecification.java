package com.engly.engly_server.specs;

import com.engly.engly_server.models.entity.Message;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static com.engly.engly_server.specs.DateSpecificationConverter.toInstantPlusOneDay;
import static com.engly.engly_server.utils.fieldvalidation.FieldUtil.isValid;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class MessageSpecification {
    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String UPDATED_AT_FIELD = "updatedAt";
    private static final String ROOM_FIELD = "room";
    private static final String USER_FIELD = "user";
    private static final String ID_FIELD = "id";
    private static final String CONTENT_FIELD = "content";
    private static final String IMAGE_URL_FIELD = "imageUrl";
    private static final String USERNAME_FIELD = "username";

    private MessageSpecification() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Specification<Message> search(String keyword) {
        return (root, _, cb) -> {
            if (isBlank(keyword)) return cb.conjunction();
            final var pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get(CONTENT_FIELD)), pattern),
                    cb.like(cb.lower(root.join(USER_FIELD).get(USERNAME_FIELD)), pattern)
            );
        };
    }

    public static Specification<Message> createdAfter(LocalDate date) {
        return (root, _, cb) -> isValid(date)
                ? cb.greaterThan(root.get(CREATED_AT_FIELD), toInstantPlusOneDay(date))
                : cb.conjunction();
    }

    public static Specification<Message> updatedAfter(LocalDate date) {
        return (root, _, cb) -> isValid(date)
                ? cb.greaterThan(root.get(UPDATED_AT_FIELD), toInstantPlusOneDay(date))
                : cb.conjunction();
    }

    public static Specification<Message> createdBefore(LocalDate date) {
        return (root, _, cb) -> isValid(date)
                ? cb.lessThan(root.get(CREATED_AT_FIELD), toInstantPlusOneDay(date))
                : cb.conjunction();
    }

    public static Specification<Message> updatedBefore(LocalDate date) {
        return (root, _, cb) -> isValid(date)
                ? cb.lessThan(root.get(UPDATED_AT_FIELD), toInstantPlusOneDay(date))
                : cb.conjunction();
    }

    public static Specification<Message> between(LocalDate min, LocalDate max) {
        return (root, _, cb) -> isValid(min) && isValid(max)
                ? cb.between(
                root.get(CREATED_AT_FIELD),
                toInstantPlusOneDay(min),
                toInstantPlusOneDay(max))
                : cb.conjunction();
    }

    public static Specification<Message> contentLike(String content) {
        return (root, _, cb) -> isNotBlank(content)
                ? cb.like(cb.lower(root.get(CONTENT_FIELD)), "%" + content.toLowerCase() + "%")
                : cb.conjunction();
    }

    public static Specification<Message> usernameLike(String username) {
        return (root, _, cb) -> isNotBlank(username)
                ? cb.like(cb.lower(root.join(USER_FIELD).get(USERNAME_FIELD)), "%" + username.toLowerCase() + "%")
                : cb.conjunction();
    }

    public static Specification<Message> userIdEquals(String userId) {
        return (root, _, cb) -> isNotBlank(userId)
                ? cb.equal(root.join(USER_FIELD).get(ID_FIELD), userId)
                : cb.conjunction();
    }

    public static Specification<Message> roomIdEquals(String roomId) {
        return (root, _, cb) -> isNotBlank(roomId)
                ? cb.equal(root.join(ROOM_FIELD).get(ID_FIELD), roomId)
                : cb.conjunction();
    }

    public static Specification<Message> roomNameLike(String roomName) {
        return (root, _, cb) -> isNotBlank(roomName)
                ? cb.like(cb.lower(root.join(ROOM_FIELD).get("name")), "%" + roomName.toLowerCase() + "%")
                : cb.conjunction();
    }

    public static Specification<Message> isEmpty() {
        return (root, _, cb) -> cb.and(
                cb.or(
                        cb.isNull(root.get(CONTENT_FIELD)),
                        cb.equal(root.get(CONTENT_FIELD), "")
                ),
                cb.isNull(root.get(IMAGE_URL_FIELD))
        );
    }
}