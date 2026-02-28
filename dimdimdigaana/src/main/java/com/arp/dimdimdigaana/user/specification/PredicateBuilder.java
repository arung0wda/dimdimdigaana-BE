package com.arp.dimdimdigaana.user.specification;

import com.arp.dimdimdigaana.user.dto.SearchCriteria;
import com.arp.dimdimdigaana.user.entity.UserEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Set;

/**
 * Strategy interface for building a JPA {@link Predicate} from a single
 * {@link SearchCriteria}.
 * <p>
 * Each implementation handles one category of field types
 * (e.g. strings, numbers, dates, virtual fields like "age").
 */
public interface PredicateBuilder {

    /** Fields that do not map to a real entity column. */
    Set<String> VIRTUAL_FIELDS = Set.of("age");

    /**
     * Returns {@code true} if this builder can handle the given criteria.
     */
    boolean supports(SearchCriteria criteria, Root<UserEntity> root);

    /**
     * Builds and returns the JPA predicate for the given criteria.
     *
     * @throws IllegalArgumentException if the operation is unsupported
     */
    Predicate build(SearchCriteria criteria, Root<UserEntity> root, CriteriaBuilder cb);
}

