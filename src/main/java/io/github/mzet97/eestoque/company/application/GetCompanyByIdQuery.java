package io.github.mzet97.eestoque.company.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-COMPANY-004. */
public record GetCompanyByIdQuery(UUID id) implements Query<BaseResult<CompanyViewModel>> {
}
