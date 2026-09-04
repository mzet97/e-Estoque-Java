package io.github.mzet97.eestoque.product.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.product.domain.ProductCriteria;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.PagedSearch;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-PROD-005. */
public record SearchProductsQuery(
        String name,
        String description,
        String shortDescription,
        BigDecimal price,
        BigDecimal weight,
        BigDecimal height,
        BigDecimal length,
        UUID idCategory,
        UUID idCompany,
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        Integer pageIndex,
        Integer pageSize) implements Query<BaseResultList<ProductViewModel>>, PagedSearch {

    public ProductCriteria toCriteria() {
        return new ProductCriteria(id, name, description, shortDescription, price, weight, height, length,
                idCategory, idCompany, createdAt, updatedAt, deletedAt, order, pageOrDefault(), sizeOrDefault());
    }
}
