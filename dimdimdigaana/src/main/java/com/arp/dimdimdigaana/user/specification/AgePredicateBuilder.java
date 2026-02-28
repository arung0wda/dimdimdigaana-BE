package com.arp.dimdimdigaana.user.specification;

import com.arp.dimdimdigaana.user.dto.SearchCriteria;
import com.arp.dimdimdigaana.user.entity.UserEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;

/**
 * Builds predicates for the virtual "age" field.
 * <p>
 * "age" is not a database column — every age-based filter is translated
 * into a date range on the {@code dob} column.
 * <p>
 * Supported operations: EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN,
 * GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, BETWEEN.
 */
public class AgePredicateBuilder implements PredicateBuilder {

    private static final String VIRTUAL_FIELD = "age";
    private static final String DOB_COLUMN = "dob";

    @Override
    public boolean supports(SearchCriteria criteria, Root<UserEntity> root) {
        return VIRTUAL_FIELD.equalsIgnoreCase(criteria.getField());
    }

    @Override
    public Predicate build(SearchCriteria criteria, Root<UserEntity> root, CriteriaBuilder cb) {
        Path<LocalDate> dobPath = root.get(DOB_COLUMN);
        LocalDate today = LocalDate.now();
        long age = Long.parseLong(criteria.getValue());

        return switch (criteria.getOperation()) {
            case EQUALS -> agEquals(cb, dobPath, today, age);
            case NOT_EQUALS -> cb.not(agEquals(cb, dobPath, today, age));
            case GREATER_THAN -> ageGreaterThan(cb, dobPath, today, age);
            case GREATER_THAN_OR_EQUAL -> ageGreaterThanOrEqual(cb, dobPath, today, age);
            case LESS_THAN -> ageLessThan(cb, dobPath, today, age);
            case LESS_THAN_OR_EQUAL -> ageLessThanOrEqual(cb, dobPath, today, age);
            case BETWEEN -> ageBetween(cb, dobPath, today, age, criteria.getValueTo());
            default -> throw new IllegalArgumentException(
                    "Operation " + criteria.getOperation() + " is not supported for 'age'");
        };
    }

    // ── Helpers ─────────────────────────────────────────────────

    /**
     * Person is exactly {@code age} years old when their dob falls in
     * {@code (today − (age+1) years + 1 day, today − age years]}.
     */
    private Predicate agEquals(CriteriaBuilder cb, Path<LocalDate> dob,
                               LocalDate today, long age) {
        LocalDate from = today.minusYears(age + 1).plusDays(1);
        LocalDate to = today.minusYears(age);
        return cb.between(dob, from, to);
    }

    /** age > X  ⟹  age >= X+1  ⟹  born on or before (today − (X+1) years) */
    private Predicate ageGreaterThan(CriteriaBuilder cb, Path<LocalDate> dob,
                                     LocalDate today, long age) {
        return cb.lessThanOrEqualTo(dob, today.minusYears(age + 1));
    }

    /** age >= X  ⟹  born on or before (today − X years) */
    private Predicate ageGreaterThanOrEqual(CriteriaBuilder cb, Path<LocalDate> dob,
                                            LocalDate today, long age) {
        return cb.lessThanOrEqualTo(dob, today.minusYears(age));
    }

    /** age < X  ⟹  born after (today − X years) */
    private Predicate ageLessThan(CriteriaBuilder cb, Path<LocalDate> dob,
                                  LocalDate today, long age) {
        return cb.greaterThan(dob, today.minusYears(age));
    }

    /** age <= X  ⟹  born after (today − (X+1) years) */
    private Predicate ageLessThanOrEqual(CriteriaBuilder cb, Path<LocalDate> dob,
                                         LocalDate today, long age) {
        return cb.greaterThan(dob, today.minusYears(age + 1));
    }

    /** age between low..high  ⟹  dob between (today − high+1 years + 1 day) and (today − low years) */
    private Predicate ageBetween(CriteriaBuilder cb, Path<LocalDate> dob,
                                 LocalDate today, long age, String valueToStr) {
        long ageTo = Long.parseLong(valueToStr);
        long low = Math.min(age, ageTo);
        long high = Math.max(age, ageTo);
        LocalDate dobFrom = today.minusYears(high + 1).plusDays(1);
        LocalDate dobTo = today.minusYears(low);
        return cb.between(dob, dobFrom, dobTo);
    }
}

