package io.github.mzet97.eestoque.inventory.application;

import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-INV-006. */
public record GridifyInventoriesQuery(String filter, String orderBy, Integer page, Integer pageSize, boolean odataDialect)
        implements Query<BaseResultList<InventoryViewModel>> {
}
