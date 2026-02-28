package com.arp.dimdimdigaana.user.specification;

import com.arp.dimdimdigaana.user.dto.SearchCriteria;
import com.arp.dimdimdigaana.user.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Builds a JPA {@link Specification} from a list of {@link SearchCriteria}.
 * <p>
 * Each individual criterion is delegated to the appropriate
 * {@link PredicateBuilder} resolved via {@link PredicateBuilderFactory}.
 * All criteria are combined with AND logic.
 */
public final class UserSpecification {

    private UserSpecification() {
    }

    /**
     * Combines every {@link SearchCriteria} into a single AND-joined
     * {@link Specification}.  An empty or {@code null} list returns all rows.
     */
    public static Specification<UserEntity> buildFromCriteria(List<SearchCriteria> criteriaList) {
        if (criteriaList == null || criteriaList.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }

        Specification<UserEntity> spec = Specification.where(toSpecification(criteriaList.get(0)));
        for (int i = 1; i < criteriaList.size(); i++) {
            spec = spec.and(toSpecification(criteriaList.get(i)));
        }
        return spec;
    }

    private static Specification<UserEntity> toSpecification(SearchCriteria criteria) {
        return (root, query, cb) -> {
            PredicateBuilder builder = PredicateBuilderFactory.resolve(criteria, root);
            return builder.build(criteria, root, cb);
        };
    }
}

