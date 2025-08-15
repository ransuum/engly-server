package com.engly.engly_server.specs;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static com.engly.engly_server.utils.fieldvalidation.FieldUtil.isValid;

public final class RoomSpecification {
    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String UPDATED_AT_FIELD = "updatedAt";
    private static final String NAME_FIELD = "name";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String CATEGORY_FIELD = "category";
    private static final String CREATOR_FIELD = "creator";
    private static final String CHAT_PARTICIPANTS_FIELD = "chatParticipants";

    private RoomSpecification() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Specification<Rooms> search(String keyword) {
        return (root, _, cb) -> {
            if (!isValid(keyword)) return cb.conjunction();
            final var pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get(NAME_FIELD)), pattern),
                    cb.like(cb.lower(root.get(DESCRIPTION_FIELD)), pattern),
                    cb.like(cb.lower(root.join(CREATOR_FIELD).get("username")), pattern)
            );
        };
    }

    public static Specification<Rooms> createdAfter(LocalDate date) {
        return (root, _, cb) ->
                isValid(date) ? cb.greaterThan(root.get(CREATED_AT_FIELD), date.atStartOfDay().toInstant(ZoneOffset.UTC)) : cb.conjunction();
    }

    public static Specification<Rooms> createdBefore(LocalDate date) {
        return ((root, _, criteriaBuilder) ->
                isValid(date) ? criteriaBuilder.lessThan(
                        root.get(CREATED_AT_FIELD), date.atStartOfDay().toInstant(ZoneOffset.UTC)) : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> between(LocalDate min, LocalDate max) {
        return ((root, _, criteriaBuilder) ->
                isValid(min) && isValid(max) ? criteriaBuilder.between(
                        root.get(CREATED_AT_FIELD),
                        min.atStartOfDay().toInstant(ZoneOffset.UTC),
                        max.atStartOfDay().toInstant(ZoneOffset.UTC)) : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> nameLike(String name) {
        return ((root, _, criteriaBuilder) ->
                isValid(name) ? criteriaBuilder.like(criteriaBuilder.lower(
                        root.get(NAME_FIELD)), "%" + name.toLowerCase() + "%") : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> descriptionLike(String description) {
        return ((root, _, criteriaBuilder) ->
                isValid(description) ? criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get(DESCRIPTION_FIELD)), "%" + description.toLowerCase() + "%") : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> categoryEquals(CategoryType category) {
        return ((root, _, criteriaBuilder) ->
                isValid(category) ? criteriaBuilder.equal(root.join(CATEGORY_FIELD).get("name"), category) : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> creatorUsernameLike(String creatorUsername) {
        return ((root, _, criteriaBuilder) ->
                isValid(creatorUsername) ? criteriaBuilder.like(
                        root.join(CREATOR_FIELD).get("username"), "%" + creatorUsername.toLowerCase() + "%") : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> participantsLessThan(Integer participants) {
        return ((root, _, criteriaBuilder) ->
                participants != null ? criteriaBuilder.lessThan(
                        root.join(CHAT_PARTICIPANTS_FIELD).get("size"), participants) : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> participantsGreaterThan(Integer participants) {
        return ((root, _, criteriaBuilder) ->
                participants != null ? criteriaBuilder.greaterThan(
                        root.join(CHAT_PARTICIPANTS_FIELD).get("size"), participants) : criteriaBuilder.conjunction());
    }
}
