package io.github.mzet97.eestoque.product.application;

import org.springframework.stereotype.Component;

import io.github.mzet97.eestoque.product.domain.CategoryRepository;
import io.github.mzet97.eestoque.product.domain.ProductRepository;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.application.query.GridifyRequestParser;
import io.github.mzet97.eestoque.shared.application.query.ODataRequestParser;

/** Handlers gridify do módulo product (FR-CAT-006 / FR-PROD-006). */
public final class GridifyHandlers {

    private GridifyHandlers() {
    }

    @Component
    public static class GridifyCategoriesHandler
            implements QueryHandler<GridifyCategoriesQuery, BaseResultList<CategoryViewModel>> {

        private final CategoryRepository repository;

        public GridifyCategoriesHandler(CategoryRepository repository) {
            this.repository = repository;
        }

        @Override
        public BaseResultList<CategoryViewModel> handle(GridifyCategoriesQuery query) {
            var result = repository.searchGridify(query.odataDialect()
                    ? ODataRequestParser.toCriteria(query.filter(), query.orderBy(), query.page(), query.pageSize())
                    : GridifyRequestParser.toCriteria(query.filter(), query.orderBy(), query.page(), query.pageSize()));
            return BaseResultList.of(result.data().stream().map(CategoryViewModel::from).toList(),
                    result.pagedResult());
        }
    }

    @Component
    public static class GridifyProductsHandler
            implements QueryHandler<GridifyProductsQuery, BaseResultList<ProductViewModel>> {

        private final ProductRepository repository;

        public GridifyProductsHandler(ProductRepository repository) {
            this.repository = repository;
        }

        @Override
        public BaseResultList<ProductViewModel> handle(GridifyProductsQuery query) {
            var result = repository.searchGridify(query.odataDialect()
                    ? ODataRequestParser.toCriteria(query.filter(), query.orderBy(), query.page(), query.pageSize())
                    : GridifyRequestParser.toCriteria(query.filter(), query.orderBy(), query.page(), query.pageSize()));
            return BaseResultList.of(result.data().stream().map(ProductViews::toViewModel).toList(),
                    result.pagedResult());
        }
    }
}
