package com.arp.dimdimdigaana.user.specification;

import com.arp.dimdimdigaana.exception.AppException;
import com.arp.dimdimdigaana.exception.ErrorCode;
import com.arp.dimdimdigaana.user.dto.SearchCriteria;
import com.arp.dimdimdigaana.user.entity.UserEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
        LocalDate date = parseDate(criteria.getValue(), criteria.getField());

        return switch (criteria.getOperation()) {
            case EQUALS -> cb.equal(path, date);
            case NOT_EQUALS -> cb.notEqual(path, date);
            case GREATER_THAN -> cb.greaterThan(path, date);
            case LESS_THAN -> cb.lessThan(path, date);
            case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(path, date);
            case LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(path, date);
            case BETWEEN -> {
                LocalDate dateTo = parseDate(criteria.getValueTo(), criteria.getField());
                yield cb.between(path, date, dateTo);
            }
            default -> throw unsupported(criteria);
        };
    }

    private static LocalDate parseDate(String raw, String field) {
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new AppException(ErrorCode.BAD_REQUEST,
                    "Invalid date value '" + raw + "' for field '" + field + "' — expected ISO-8601 (yyyy-MM-dd)", e);
        }
    }

    private static IllegalArgumentException unsupported(SearchCriteria c) {
        return new IllegalArgumentException(
                "Operation " + c.getOperation() + " is not supported for date field '" + c.getField() + "'");
    }
}

