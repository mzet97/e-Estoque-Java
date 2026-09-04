package io.github.mzet97.eestoque.product.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-CAT-004. */
public record GetCategoryByIdQuery(UUID id) implements Query<BaseResult<CategoryViewModel>> {
}
