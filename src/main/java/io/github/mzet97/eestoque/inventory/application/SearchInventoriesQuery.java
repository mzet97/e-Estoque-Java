package io.github.mzet97.eestoque.inventory.application;

import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.inventory.domain.InventoryCriteria;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.PagedSearch;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-INV-005 — dateOrder como texto (coluna varchar do schema original). */
public record SearchInventoriesQuery(
        Integer quantity,
        String dateOrder,
        UUID idProduct,
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        Integer pageIndex,
        Integer pageSize) implements Query<BaseResultList<InventoryViewModel>>, PagedSearch {

    public InventoryCriteria toCriteria() {
        return new InventoryCriteria(id, quantityOrDefault(), dateOrder, idProduct,
                createdAt, updatedAt, deletedAt, order, pageOrDefault(), sizeOrDefault());
    }

    private Integer quantityOrDefault() {
        return quantity == null || quantity == 0 ? null : quantity;
    }
}
