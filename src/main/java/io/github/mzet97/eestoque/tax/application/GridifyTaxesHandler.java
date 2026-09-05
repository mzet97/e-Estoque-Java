package io.github.mzet97.eestoque.tax.application;

import org.springframework.stereotype.Component;

import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.application.query.GridifyRequestParser;
import io.github.mzet97.eestoque.shared.application.query.ODataRequestParser;
import io.github.mzet97.eestoque.tax.domain.TaxRepository;

/** FR-TAX-006. */
@Component
public class GridifyTaxesHandler implements QueryHandler<GridifyTaxesQuery, BaseResultList<TaxViewModel>> {

    private final TaxRepository repository;

    public GridifyTaxesHandler(TaxRepository repository) {
        this.repository = repository;
    }

    @Override
    public BaseResultList<TaxViewModel> handle(GridifyTaxesQuery query) {
        var result = repository.searchGridify(query.odataDialect()
                ? ODataRequestParser.toCriteria(query.filter(), query.orderBy(), query.page(), query.pageSize())
                : GridifyRequestParser.toCriteria(query.filter(), query.orderBy(), query.page(), query.pageSize()));
        return BaseResultList.of(result.data().stream().map(TaxViews::toViewModel).toList(), result.pagedResult());
    }
}
