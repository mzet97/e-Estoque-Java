package io.github.mzet97.eestoque.tax.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-TAX-004. */
public record GetTaxByIdQuery(UUID id) implements Query<BaseResult<TaxViewModel>> {
}
