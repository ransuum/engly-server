package com.engly.engly_server.specs;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class RoomSpecification {

    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String UPDATED_AT_FIELD = "updatedAt";
    private static final String NAME_FIELD = "name";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String CATEGORY_NAME_PATH = "category.name";
    private static final String CREATOR_ID_PATH = "creator.id";
    private static final String CREATOR_USERNAME_PATH = "creator.username";
    private static final String CHAT_PARTICIPANTS_FIELD = "chatParticipants";

    private RoomSpecification() { }

    public static Specification<Rooms> hasCategory(CategoryType categoryType) {
        return createEqualSpecification(CATEGORY_NAME_PATH, categoryType);
    }

    public static Specification<Rooms> hasNameContaining(String name) {
        return createLikeSpecification(NAME_FIELD, name);
    }

    public static Specification<Rooms> hasDescriptionContaining(String description) {
        return createLikeSpecification(DESCRIPTION_FIELD, description);
    }

    public static Specification<Rooms> hasKeywordInNameOrDescription(String keyword) {
        return (root, _, criteriaBuilder) -> {
            if (!FieldUtil.isValid(keyword)) return null;

            var lowerKeyword = "%" + keyword.toLowerCase() + "%";
            var nameCondition = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(NAME_FIELD)), lowerKeyword
            );
            var descriptionCondition = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(DESCRIPTION_FIELD)), lowerKeyword
            );

            return criteriaBuilder.or(nameCondition, descriptionCondition);
        };
    }

    public static Specification<Rooms> createdAfter(LocalDate date) {
        return createDateAfterSpecification(CREATED_AT_FIELD, date);
    }

    public static Specification<Rooms> createdBefore(LocalDate date) {
        return createDateBeforeSpecification(CREATED_AT_FIELD, date);
    }

    public static Specification<Rooms> createdBetween(LocalDate startDate, LocalDate endDate) {
        return createDateBetweenSpecification(CREATED_AT_FIELD, startDate, endDate);
    }

    public static Specification<Rooms> updatedAfter(LocalDate date) {
        return createDateAfterSpecification(UPDATED_AT_FIELD, date);
    }

    public static Specification<Rooms> updatedBefore(LocalDate date) {
        return createDateBeforeSpecification(UPDATED_AT_FIELD, date);
    }

    public static Specification<Rooms> updatedBetween(LocalDate startDate, LocalDate endDate) {
        return createDateBetweenSpecification(UPDATED_AT_FIELD, startDate, endDate);
    }

    public static Specification<Rooms> hasCreator(String creatorId) {
        return createEqualSpecification(CREATOR_ID_PATH, creatorId);
    }

    public static Specification<Rooms> hasCreatorName(String creatorName) {
        return createLikeSpecification(CREATOR_USERNAME_PATH, creatorName);
    }

    public static Specification<Rooms> hasMinimumParticipants(Integer minParticipants) {
        return createSizeComparisonSpecification(minParticipants, true);
    }

    public static Specification<Rooms> hasMaximumParticipants(Integer maxParticipants) {
        return createSizeComparisonSpecification(maxParticipants, false);
    }

    private static <T> Specification<Rooms> createEqualSpecification(String fieldPath, T value) {
        return (root, _, criteriaBuilder) -> {
            if (!FieldUtil.isValid(value)) return null;
            return criteriaBuilder.equal(getNestedField(root, fieldPath), value);
        };
    }

    private static Specification<Rooms> createLikeSpecification(String fieldPath, String value) {
        return (root, _, criteriaBuilder) -> {
            if (!FieldUtil.isValid(value)) return null;

            var lowerValue = "%" + value.toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(getNestedField(root, fieldPath)),
                    lowerValue
            );
        };
    }

    private static Specification<Rooms> createDateAfterSpecification(String dateField, LocalDate date) {
        return (root, _, criteriaBuilder) -> {
            if (!FieldUtil.isValid(date)) return null;
            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get(dateField),
                    toInstant(date)
            );
        };
    }

    private static Specification<Rooms> createDateBeforeSpecification(String dateField, LocalDate date) {
        return (root, _, criteriaBuilder) -> {
            if (!FieldUtil.isValid(date)) return null;
            return criteriaBuilder.lessThan(
                    root.get(dateField),
                    toInstant(date.plusDays(1))
            );
        };
    }

    private static Specification<Rooms> createDateBetweenSpecification(String dateField, LocalDate startDate, LocalDate endDate) {
        return (root, _, criteriaBuilder) -> {
            if (!FieldUtil.isValid(startDate) && !FieldUtil.isValid(endDate)) return null;

            var conditions = new ArrayList<Predicate>();

            if (FieldUtil.isValid(startDate)) {
                conditions.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get(dateField),
                        toInstant(startDate)
                ));
            }

            if (FieldUtil.isValid(endDate)) {
                conditions.add(criteriaBuilder.lessThan(
                        root.get(dateField),
                        toInstant(endDate.plusDays(1))
                ));
            }

            return criteriaBuilder.and(conditions.toArray(new Predicate[0]));
        };
    }

    private static Specification<Rooms> createSizeComparisonSpecification(Integer value, boolean isMinimum) {
        return (root, _, criteriaBuilder) -> {
            if (value == null) return null;

            var sizeExpression = criteriaBuilder.size(root.get(RoomSpecification.CHAT_PARTICIPANTS_FIELD));
            return isMinimum
                    ? criteriaBuilder.greaterThanOrEqualTo(sizeExpression, value)
                    : criteriaBuilder.lessThanOrEqualTo(sizeExpression, value);
        };
    }

    private static Instant toInstant(LocalDate date) {
        return date.atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    @SuppressWarnings("unchecked")
    private static <T> Path<T> getNestedField(Root<Rooms> root, String fieldPath) {
        final String[] parts = fieldPath.split("\\.");
        Path<?> path = root;

        for (String part : parts) path = path.get(part);

        return (Path<T>) path;
    }
}
