package io.github.mzet97.eestoque.product.application;

import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.product.domain.CategoryCriteria;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.PagedSearch;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-CAT-005 — filtros de igualdade exata + ordenação whitelist + paginação. */
public record SearchCategoriesQuery(
        String name,
        String description,
        String shortDescription,
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        Integer pageIndex,
        Integer pageSize) implements Query<BaseResultList<CategoryViewModel>>, PagedSearch {

    public CategoryCriteria toCriteria() {
        return new CategoryCriteria(id, name, description, shortDescription, createdAt, updatedAt, deletedAt,
                order, pageOrDefault(), sizeOrDefault());
    }
}
