package io.github.mzet97.eestoque.company.application;

import org.springframework.stereotype.Component;

import io.github.mzet97.eestoque.company.domain.CompanyRepository;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.infrastructure.web.query.GridifyRequestParser;

/** FR-COMPANY-006. */
@Component
public class GridifyCompaniesHandler implements QueryHandler<GridifyCompaniesQuery, BaseResultList<CompanyViewModel>> {

    private final CompanyRepository repository;

    public GridifyCompaniesHandler(CompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Class<GridifyCompaniesQuery> queryType() {
        return GridifyCompaniesQuery.class;
    }

    @Override
    public BaseResultList<CompanyViewModel> handle(GridifyCompaniesQuery query) {
        var result = repository.searchGridify(GridifyRequestParser.toCriteria(query.filter(), query.orderBy(),
                query.page(), query.pageSize()));
        return BaseResultList.of(result.data().stream().map(CompanyViewModel::from).toList(), result.pagedResult());
    }
}
