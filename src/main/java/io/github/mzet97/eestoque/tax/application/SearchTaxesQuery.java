package io.github.mzet97.eestoque.tax.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.PagedSearch;
import io.github.mzet97.eestoque.shared.application.Query;
import io.github.mzet97.eestoque.tax.domain.TaxCriteria;

/** FR-TAX-005. */
public record SearchTaxesQuery(
        String name,
        String description,
        BigDecimal percentage,
        UUID idCategory,
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        Integer pageIndex,
        Integer pageSize) implements Query<BaseResultList<TaxViewModel>>, PagedSearch {

    public TaxCriteria toCriteria() {
        return new TaxCriteria(id, name, description, percentageOrDefault(), idCategory, createdAt, updatedAt,
                deletedAt, order, pageOrDefault(), sizeOrDefault());
    }

    private BigDecimal percentageOrDefault() {
        return percentage == null || percentage.signum() == 0 ? null : percentage;
    }
}
