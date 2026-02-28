package com.arp.dimdimdigaana.user.specification;

import com.arp.dimdimdigaana.user.dto.SearchCriteria;
import com.arp.dimdimdigaana.user.entity.UserEntity;
import jakarta.persistence.criteria.*;

/**
 * Builds predicates for {@link String} entity fields.
 * <p>
 * All comparisons are case-insensitive.
 * Supported operations: EQUALS, NOT_EQUALS, CONTAINS, STARTS_WITH, ENDS_WITH.
 */
public class StringPredicateBuilder implements PredicateBuilder {

    @Override
    public boolean supports(SearchCriteria criteria, Root<UserEntity> root) {
        return !VIRTUAL_FIELDS.contains(criteria.getField().toLowerCase())
                && root.get(criteria.getField()).getJavaType().equals(String.class);
    }

    @Override
    public Predicate build(SearchCriteria criteria, Root<UserEntity> root, CriteriaBuilder cb) {
        Expression<String> lower = cb.lower(root.get(criteria.getField()));
        String lowerVal = criteria.getValue().toLowerCase();

        return switch (criteria.getOperation()) {
            case EQUALS -> cb.equal(lower, lowerVal);
            case NOT_EQUALS -> cb.notEqual(lower, lowerVal);
            case CONTAINS -> cb.like(lower, "%" + lowerVal + "%");
            case STARTS_WITH -> cb.like(lower, lowerVal + "%");
            case ENDS_WITH -> cb.like(lower, "%" + lowerVal);
            default -> throw unsupported(criteria);
        };
    }

    private static IllegalArgumentException unsupported(SearchCriteria c) {
        return new IllegalArgumentException(
                "Operation " + c.getOperation() + " is not supported for string field '" + c.getField() + "'");
    }
}


