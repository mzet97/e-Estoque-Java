package io.github.mzet97.eestoque.inventory.application;

import org.springframework.stereotype.Component;

import io.github.mzet97.eestoque.inventory.domain.InventoryRepository;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.application.query.GridifyRequestParser;
import io.github.mzet97.eestoque.shared.application.query.ODataRequestParser;

/** FR-INV-006. */
@Component
public class GridifyInventoriesHandler
        implements QueryHandler<GridifyInventoriesQuery, BaseResultList<InventoryViewModel>> {

    private final InventoryRepository repository;

    public GridifyInventoriesHandler(InventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public BaseResultList<InventoryViewModel> handle(GridifyInventoriesQuery query) {
        var result = repository.searchGridify(query.odataDialect()
                ? ODataRequestParser.toCriteria(query.filter(), query.orderBy(), query.page(), query.pageSize())
                : GridifyRequestParser.toCriteria(query.filter(), query.orderBy(), query.page(), query.pageSize()));
        return BaseResultList.of(result.data().stream().map(InventoryViews::toViewModel).toList(),
                result.pagedResult());
    }
}
