package io.github.mzet97.eestoque.company.application;

import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-COMPANY-006. */
public record GridifyCompaniesQuery(String filter, String orderBy, Integer page, Integer pageSize)
        implements Query<BaseResultList<CompanyViewModel>> {
}
