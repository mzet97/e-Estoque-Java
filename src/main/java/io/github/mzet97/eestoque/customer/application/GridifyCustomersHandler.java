package io.github.mzet97.eestoque.customer.application;

import org.springframework.stereotype.Component;

import io.github.mzet97.eestoque.customer.domain.CustomerRepository;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.infrastructure.web.query.GridifyRequestParser;

/** FR-CUSTOMER-006. */
@Component
public class GridifyCustomersHandler
        implements QueryHandler<GridifyCustomersQuery, BaseResultList<CustomerViewModel>> {

    private final CustomerRepository repository;

    public GridifyCustomersHandler(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Class<GridifyCustomersQuery> queryType() {
        return GridifyCustomersQuery.class;
    }

    @Override
    public BaseResultList<CustomerViewModel> handle(GridifyCustomersQuery query) {
        var result = repository.searchGridify(GridifyRequestParser.toCriteria(query.filter(), query.orderBy(),
                query.page(), query.pageSize()));
        return BaseResultList.of(result.data().stream().map(CustomerViewModel::from).toList(), result.pagedResult());
    }
}
