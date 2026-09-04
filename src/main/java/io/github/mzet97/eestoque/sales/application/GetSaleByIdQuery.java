package io.github.mzet97.eestoque.sales.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-SALE-004. */
public record GetSaleByIdQuery(UUID id) implements Query<BaseResult<SaleViewModel>> {
}
