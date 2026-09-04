package io.github.mzet97.eestoque.shared.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * Montagem de Specifications para as buscas de igualdade exata do contrato
 * original (filtros opcionais + ordenação por whitelist, default id).
 */
public final class SpecificationSearch {

    private SpecificationSearch() {
    }

    @FunctionalInterface
    public interface FilterApplier<T> {
        void apply(CriteriaBuilder cb, Root<T> root, List<Predicate> predicates);
    }

    public static <T> Specification<T> withFilters(FilterApplier<T> applier) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            applier.apply(cb, root, predicates);
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static <T> void equalIfNotNull(CriteriaBuilder cb, Root<T> root, List<Predicate> predicates,
                                          Object value, String attribute) {
        if (value == null) {
            return;
        }
        predicates.add(cb.equal(root.get(attribute), value));
    }

    public static <T> void equalIfNotBlank(CriteriaBuilder cb, Root<T> root, List<Predicate> predicates,
                                           String value, String attribute) {
        if (value == null || value.isBlank()) {
            return;
        }
        predicates.add(cb.equal(root.get(attribute), value));
    }

    /** Ordenação com whitelist: apenas atributos mapeados são aceitos; default = id ASC. */
    public static <T> Pageable pageable(String order, Map<String, String> orderMap, int page, int size) {
        var sort = sortFrom(order, orderMap);
        return PageRequest.of(page - 1, size, sort);
    }

    public static Sort sortFrom(String order, Map<String, String> orderMap) {
        if (order == null || order.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }
        var attribute = orderMap.get(order);
        if (attribute == null) {
            return Sort.by(Sort.Direction.ASC, "id");
        }
        return Sort.by(Sort.Direction.ASC, attribute);
    }
}
