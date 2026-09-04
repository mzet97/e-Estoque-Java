package io.github.mzet97.eestoque.sales.application;

import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-SALE-006. */
public record GridifySalesQuery(String filter, String orderBy, Integer page, Integer pageSize, boolean odataDialect)
        implements Query<BaseResultList<SaleViewModel>> {
}
