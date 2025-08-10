package com.engly.engly_server.specs;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class RoomSpecification {

    private static final String DATE_FIELD = "createdAt";

    private RoomSpecification() { }

    public static Specification<Rooms> hasCategory(CategoryType categoryType) {
        return (root, _, criteriaBuilder) ->
                FieldUtil.isValid(categoryType) ? criteriaBuilder.equal(root.get("category").get("name"), categoryType) : null;
    }

    public static Specification<Rooms> hasNameContaining(String name) {
        return (root, _, criteriaBuilder) ->
                FieldUtil.isValid(name) ? criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                ) : null;

    }

    public static Specification<Rooms> hasDescriptionContaining(String description) {
        return (root, _, criteriaBuilder) ->
                FieldUtil.isValid(description) ?
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("description")),
                                "%" + description.toLowerCase() + "%"
                        ) : null;
    }

    public static Specification<Rooms> hasKeywordInNameOrDescription(String keyword) {
        return (root, _, criteriaBuilder) -> {
            if (!FieldUtil.isValid(keyword)) return null;

            var lowerKeyword = "%" + keyword.toLowerCase() + "%";
            var nameCondition = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), lowerKeyword
            );
            var descriptionCondition = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), lowerKeyword
            );

            return criteriaBuilder.or(nameCondition, descriptionCondition);
        };
    }

    public static Specification<Rooms> createdAfter(LocalDate date) {
        return (root, _, criteriaBuilder) ->
                FieldUtil.isValid(date) ?
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get(DATE_FIELD),
                                date.atStartOfDay().toInstant(ZoneOffset.UTC)
                        ) : null;
    }

    public static Specification<Rooms> createdBefore(LocalDate date) {
        return (root, _, criteriaBuilder) ->
                FieldUtil.isValid(date) ?
                        criteriaBuilder.lessThan(
                                root.get(DATE_FIELD),
                                date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)
                        ) : null;
    }

    public static Specification<Rooms> createdBetween(LocalDate startDate, LocalDate endDate) {
        return (root, _, criteriaBuilder) -> {
            if (!FieldUtil.isValid(startDate) && !FieldUtil.isValid(endDate)) return null;

            var conditions = new ArrayList<Predicate>();

            if (startDate != null)
                conditions.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get(DATE_FIELD),
                        startDate.atStartOfDay().toInstant(ZoneOffset.UTC)
                ));

            if (endDate != null)
                conditions.add(criteriaBuilder.lessThan(
                        root.get(DATE_FIELD),
                        endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)
                ));

            return criteriaBuilder.and(conditions.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    public static Specification<Rooms> hasCreator(String creatorId) {
        return (root, _, criteriaBuilder) ->
                FieldUtil.isValid(creatorId) ? criteriaBuilder.equal(root.get("creator").get("id"), creatorId) : null;
    }

    public static Specification<Rooms> hasCreatorName(String creatorName) {
        return (root, _, criteriaBuilder) ->
                FieldUtil.isValid(creatorName) ?
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("creator").get("username")),
                                "%" + creatorName.toLowerCase() + "%"
                        ) : null;
    }

    public static Specification<Rooms> hasMinimumParticipants(Integer minParticipants) {
        return (root, _, criteriaBuilder) ->
                minParticipants == null ? null :
                        criteriaBuilder.greaterThanOrEqualTo(
                                criteriaBuilder.size(root.get("chatParticipants")),
                                minParticipants
                        );
    }

    public static Specification<Rooms> hasMaximumParticipants(Integer maxParticipants) {
        return (root, _, criteriaBuilder) ->
                maxParticipants == null ? null :
                        criteriaBuilder.lessThanOrEqualTo(
                                criteriaBuilder.size(root.get("chatParticipants")),
                                maxParticipants
                        );
    }

    public static Specification<Rooms> updatedAfter(Instant instant) {
        return (root, _, criteriaBuilder) ->
                instant == null ? null :
                        criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), instant);
    }
}
