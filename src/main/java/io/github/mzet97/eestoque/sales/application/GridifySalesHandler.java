package io.github.mzet97.eestoque.sales.application;

import org.springframework.stereotype.Component;

import io.github.mzet97.eestoque.sales.domain.SaleRepository;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.application.query.GridifyRequestParser;
import io.github.mzet97.eestoque.shared.application.query.ODataRequestParser;

/** FR-SALE-006. */
@Component
public class GridifySalesHandler implements QueryHandler<GridifySalesQuery, BaseResultList<SaleViewModel>> {

    private final SaleRepository repository;

    public GridifySalesHandler(SaleRepository repository) {
        this.repository = repository;
    }

    @Override
    public BaseResultList<SaleViewModel> handle(GridifySalesQuery query) {
        var result = repository.searchGridify(query.odataDialect()
                ? ODataRequestParser.toCriteria(query.filter(), query.orderBy(), query.page(), query.pageSize())
                : GridifyRequestParser.toCriteria(query.filter(), query.orderBy(), query.page(), query.pageSize()));
        return BaseResultList.of(result.data().stream().map(SaleViews::toViewModel).toList(), result.pagedResult());
    }
}
