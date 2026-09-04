package io.github.mzet97.eestoque.product.application;

import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-CAT-006 — busca gridify (ADR-011). */
public record GridifyCategoriesQuery(String filter, String orderBy, Integer page, Integer pageSize, boolean odataDialect)
        implements Query<BaseResultList<CategoryViewModel>> {
}
