package io.github.mzet97.eestoque.product.application;

import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-PROD-006. */
public record GridifyProductsQuery(String filter, String orderBy, Integer page, Integer pageSize)
        implements Query<BaseResultList<ProductViewModel>> {
}
