package com.engly.engly_server.specs;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static com.engly.engly_server.specs.DateSpecificationConverter.toInstantPlusOneDay;
import static com.engly.engly_server.utils.fieldvalidation.FieldUtil.isValid;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class RoomSpecification {
    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String NAME_FIELD = "name";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String CATEGORY_FIELD = "categoryId";
    private static final String CREATOR_FIELD = "creator";

    private RoomSpecification() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Specification<Rooms> search(String keyword) {
        return (root, _, cb) -> {
            if (isBlank(keyword)) return cb.conjunction();
            final var pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get(NAME_FIELD)), pattern),
                    cb.like(cb.lower(root.get(DESCRIPTION_FIELD)), pattern),
                    cb.like(cb.lower(root.join(CREATOR_FIELD).get("username")), pattern)
            );
        };
    }

    public static Specification<Rooms> createdAfter(LocalDate date) {
        return (root, _, cb) -> isValid(date)
                ? cb.greaterThan(root.get(CREATED_AT_FIELD), toInstantPlusOneDay(date))
                : cb.conjunction();
    }

    public static Specification<Rooms> createdBefore(LocalDate date) {
        return ((root, _, criteriaBuilder) -> isValid(date)
                ? criteriaBuilder.lessThan(root.get(CREATED_AT_FIELD), toInstantPlusOneDay(date))
                : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> between(LocalDate min, LocalDate max) {
        return ((root, _, criteriaBuilder) -> isValid(min) && isValid(max)
                ? criteriaBuilder.between(
                root.get(CREATED_AT_FIELD),
                toInstantPlusOneDay(min), toInstantPlusOneDay(max))
                : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> nameLike(String name) {
        return ((root, _, criteriaBuilder) -> isNotBlank(name)
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get(NAME_FIELD)), "%" + name.toLowerCase() + "%")
                : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> descriptionLike(String description) {
        return ((root, _, criteriaBuilder) -> isNotBlank(description)
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get(DESCRIPTION_FIELD)), "%" + description.toLowerCase() + "%")
                : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> categoryEquals(String categoryId) {
        return ((root, _, criteriaBuilder) -> StringUtils.isNotBlank(categoryId)
                ? criteriaBuilder.equal(root.get(CATEGORY_FIELD), categoryId)
                : criteriaBuilder.conjunction());
    }

    public static Specification<Rooms> creatorUsernameLike(String creatorUsername) {
        return ((root, _, criteriaBuilder) -> isNotBlank(creatorUsername)
                ? criteriaBuilder.like(root.join(CREATOR_FIELD).get("username"), "%" + creatorUsername.toLowerCase() + "%")
                : criteriaBuilder.conjunction());
    }
}
