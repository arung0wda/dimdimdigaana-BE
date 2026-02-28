package com.arp.dimdimdigaana.user.specification;

import com.arp.dimdimdigaana.user.dto.SearchCriteria;
import com.arp.dimdimdigaana.user.entity.UserEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;

/**
 * Builds predicates for {@link LocalDate} entity fields.
 * <p>
 * Supported operations: EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN,
 * GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, BETWEEN.
 */
public class DatePredicateBuilder implements PredicateBuilder {

    @Override
    public boolean supports(SearchCriteria criteria, Root<UserEntity> root) {
        return !VIRTUAL_FIELDS.contains(criteria.getField().toLowerCase())
                && root.get(criteria.getField()).getJavaType().equals(LocalDate.class);
    }

    @Override
    public Predicate build(SearchCriteria criteria, Root<UserEntity> root, CriteriaBuilder cb) {
        Path<LocalDate> path = root.get(criteria.getField());
        LocalDate date = LocalDate.parse(criteria.getValue());

        return switch (criteria.getOperation()) {
            case EQUALS -> cb.equal(path, date);
            case NOT_EQUALS -> cb.notEqual(path, date);
            case GREATER_THAN -> cb.greaterThan(path, date);
            case LESS_THAN -> cb.lessThan(path, date);
            case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(path, date);
            case LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(path, date);
            case BETWEEN -> {
                LocalDate dateTo = LocalDate.parse(criteria.getValueTo());
                yield cb.between(path, date, dateTo);
            }
            default -> throw unsupported(criteria);
        };
    }


    private static IllegalArgumentException unsupported(SearchCriteria c) {
        return new IllegalArgumentException(
                "Operation " + c.getOperation() + " is not supported for date field '" + c.getField() + "'");
    }
}

