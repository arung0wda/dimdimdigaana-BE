package com.arp.dimdimdigaana.user.specification;

import com.arp.dimdimdigaana.user.dto.SearchCriteria;
import com.arp.dimdimdigaana.user.entity.UserEntity;
import jakarta.persistence.criteria.Root;

import java.util.List;

/**
 * Resolves the appropriate {@link PredicateBuilder} for a given
 * {@link SearchCriteria} by iterating over registered strategies.
 * <p>
 * Builders are evaluated in registration order; the first one whose
 * {@link PredicateBuilder#supports} returns {@code true} wins.
 * The virtual "age" builder is registered first so it is matched
 * before the metamodel-based builders.
 */
public final class PredicateBuilderFactory {

    private static final List<PredicateBuilder> BUILDERS = List.of(
            new AgePredicateBuilder(),      // virtual field — checked first
            new StringPredicateBuilder(),
            new NumericPredicateBuilder(),
            new DatePredicateBuilder()
    );

    private PredicateBuilderFactory() {
    }

    /**
     * Returns the first builder that supports the given criteria.
     *
     * @throws IllegalArgumentException if no builder matches
     */
    public static PredicateBuilder resolve(SearchCriteria criteria, Root<UserEntity> root) {
        for (PredicateBuilder builder : BUILDERS) {
            if (builder.supports(criteria, root)) {
                return builder;
            }
        }
        throw new IllegalArgumentException(
                "No predicate builder found for field '" + criteria.getField() + "'");
    }
}

