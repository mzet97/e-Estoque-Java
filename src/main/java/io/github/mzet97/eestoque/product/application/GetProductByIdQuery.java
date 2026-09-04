package io.github.mzet97.eestoque.product.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-PROD-004. */
public record GetProductByIdQuery(UUID id) implements Query<BaseResult<ProductViewModel>> {
}
