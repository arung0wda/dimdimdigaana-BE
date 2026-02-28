package com.arp.dimdimdigaana.user.specification;

import com.arp.dimdimdigaana.user.dto.SearchCriteria;
import com.arp.dimdimdigaana.user.entity.UserEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Builds predicates for {@link Long} (numeric) entity fields.
 * <p>
 * Supported operations: EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN,
 * GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, BETWEEN.
 */
public class NumericPredicateBuilder implements PredicateBuilder {

    private static final Class<?>[] NUMERIC_TYPES = {Long.class, long.class};

    @Override
    public boolean supports(SearchCriteria criteria, Root<UserEntity> root) {
        if (VIRTUAL_FIELDS.contains(criteria.getField().toLowerCase())) {
            return false;
        }
        Class<?> type = root.get(criteria.getField()).getJavaType();
        for (Class<?> numeric : NUMERIC_TYPES) {
            if (numeric.equals(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Predicate build(SearchCriteria criteria, Root<UserEntity> root, CriteriaBuilder cb) {
        Path<Long> path = root.get(criteria.getField());
        Long val = Long.parseLong(criteria.getValue());

        return switch (criteria.getOperation()) {
            case EQUALS -> cb.equal(path, val);
            case NOT_EQUALS -> cb.notEqual(path, val);
            case GREATER_THAN -> cb.greaterThan(path, val);
            case LESS_THAN -> cb.lessThan(path, val);
            case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(path, val);
            case LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(path, val);
            case BETWEEN -> {
                Long valTo = Long.parseLong(criteria.getValueTo());
                yield cb.between(path, val, valTo);
            }
            default -> throw unsupported(criteria);
        };
    }


    private static IllegalArgumentException unsupported(SearchCriteria c) {
        return new IllegalArgumentException(
                "Operation " + c.getOperation() + " is not supported for numeric field '" + c.getField() + "'");
    }
}

