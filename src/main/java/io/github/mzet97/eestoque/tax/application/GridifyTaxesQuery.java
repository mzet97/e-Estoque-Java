package io.github.mzet97.eestoque.tax.application;

import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-TAX-006. */
public record GridifyTaxesQuery(String filter, String orderBy, Integer page, Integer pageSize)
        implements Query<BaseResultList<TaxViewModel>> {
}
