package io.github.mzet97.eestoque.product.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.mzet97.eestoque.product.application.CategoryViewModel;
import io.github.mzet97.eestoque.product.application.GridifyCategoriesQuery;
import io.github.mzet97.eestoque.product.application.GridifyProductsQuery;
import io.github.mzet97.eestoque.product.application.GetCategoryByIdQuery;
import io.github.mzet97.eestoque.product.application.GetProductByIdQuery;
import io.github.mzet97.eestoque.product.application.ProductViewModel;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.QueryBus;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;
import io.github.mzet97.eestoque.shared.infrastructure.web.query.ODataCollection;
import io.github.mzet97.eestoque.shared.infrastructure.web.query.ODataRequestParser;

/**
 * FR-ODATA-001/005: /odata/Categories e /odata/Products (somente leitura,
 * subset $filter/$orderby/$top/$skip/$count — ADR-011).
 */
@RestController
public class ODataProductControllers {

    private final QueryBus queries;

    public ODataProductControllers(QueryBus queries) {
        this.queries = queries;
    }

    @RestController
    @RequestMapping("/odata/Products")
    public static class Products {

        private final QueryBus queries;

        Products(QueryBus queries) {
            this.queries = queries;
        }

        @GetMapping
        public Map<String, Object> list(
                @RequestParam(name = "$filter", required = false) String filter,
                @RequestParam(name = "$orderby", required = false) String orderBy,
                @RequestParam(name = "$top", required = false) Integer top,
                @RequestParam(name = "$skip", required = false) Integer skip,
                @RequestParam(name = "$count", defaultValue = "false") boolean count) {
            var criteria = ODataRequestParser.toCriteria(filter, orderBy, top, skip);
            var result = queries.dispatch(new GridifyProductsQuery(filter, orderBy, criteria.page(), criteria.size()));
            return ODataCollection.of(result.data(), count, result.pagedResult().rowCount());
        }

        @GetMapping("/{key}")
        public ProductViewModel byKey(@PathVariable String key) {
            var id = UUID.fromString(key.replace("(", "").replace(")", ""));
            return queries.dispatch(new GetProductByIdQuery(id)).data();
        }
    }

    @RestController
    @RequestMapping("/odata/Categories")
    public static class Categories {

        private final QueryBus queries;

        Categories(QueryBus queries) {
            this.queries = queries;
        }

        @GetMapping
        public Map<String, Object> list(
                @RequestParam(name = "$filter", required = false) String filter,
                @RequestParam(name = "$orderby", required = false) String orderBy,
                @RequestParam(name = "$top", required = false) Integer top,
                @RequestParam(name = "$skip", required = false) Integer skip,
                @RequestParam(name = "$count", defaultValue = "false") boolean count) {
            var criteria = ODataRequestParser.toCriteria(filter, orderBy, top, skip);
            var result = queries.dispatch(new GridifyCategoriesQuery(filter, orderBy, criteria.page(),
                    criteria.size()));
            return ODataCollection.of(result.data(), count, result.pagedResult().rowCount());
        }

        @GetMapping("/{key}")
        public CategoryViewModel byKey(@PathVariable String key) {
            var id = UUID.fromString(key.replace("(", "").replace(")", ""));
            return queries.dispatch(new GetCategoryByIdQuery(id)).data();
        }
    }
}
