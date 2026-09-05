package io.github.mzet97.eestoque.product.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.mzet97.eestoque.product.application.CategoryViewModel;
import io.github.mzet97.eestoque.product.application.GetCategoryByIdHandler;
import io.github.mzet97.eestoque.product.application.GetCategoryByIdQuery;
import io.github.mzet97.eestoque.product.application.GridifyCategoriesQuery;
import io.github.mzet97.eestoque.product.application.GridifyHandlers.GridifyCategoriesHandler;
import io.github.mzet97.eestoque.product.application.GridifyHandlers.GridifyProductsHandler;
import io.github.mzet97.eestoque.product.application.GridifyProductsQuery;
import io.github.mzet97.eestoque.product.application.GetProductByIdQuery;
import io.github.mzet97.eestoque.product.application.ProductHandlers.GetProductByIdHandler;
import io.github.mzet97.eestoque.product.application.ProductViewModel;
import io.github.mzet97.eestoque.shared.application.query.ODataRequestParser;
import io.github.mzet97.eestoque.shared.infrastructure.web.query.ODataCollection;

/**
 * FR-ODATA-001/005: /odata/Products e /odata/Categories (somente leitura,
 * subset $filter/$orderby/$top/$skip/$count — ADR-011).
 */
public final class ODataProductControllers {

    private ODataProductControllers() {
    }

    @RestController
    @RequestMapping("/odata/Products")
    public static class Products {

        private final GridifyProductsHandler searchHandler;
        private final GetProductByIdHandler getByIdHandler;

        public Products(GridifyProductsHandler searchHandler, GetProductByIdHandler getByIdHandler) {
            this.searchHandler = searchHandler;
            this.getByIdHandler = getByIdHandler;
        }

        @GetMapping
        public Map<String, Object> list(
                @RequestParam(name = "$filter", required = false) String filter,
                @RequestParam(name = "$orderby", required = false) String orderBy,
                @RequestParam(name = "$top", required = false) Integer top,
                @RequestParam(name = "$skip", required = false) Integer skip,
                @RequestParam(name = "$count", defaultValue = "false") boolean count) {
            var result = searchHandler.handle(new GridifyProductsQuery(filter, orderBy, top, skip, true));
            return ODataCollection.of(result.data(), count, result.pagedResult().rowCount());
        }

        @GetMapping("/{key:.+}")
        public ProductViewModel byKey(@PathVariable String key) {
            var id = UUID.fromString(key.replace("(", "").replace(")", ""));
            return getByIdHandler.handle(new GetProductByIdQuery(id)).data();
        }
    }

    @RestController
    @RequestMapping("/odata/Categories")
    public static class Categories {

        private final GridifyCategoriesHandler searchHandler;
        private final GetCategoryByIdHandler getByIdHandler;

        public Categories(GridifyCategoriesHandler searchHandler, GetCategoryByIdHandler getByIdHandler) {
            this.searchHandler = searchHandler;
            this.getByIdHandler = getByIdHandler;
        }

        @GetMapping
        public Map<String, Object> list(
                @RequestParam(name = "$filter", required = false) String filter,
                @RequestParam(name = "$orderby", required = false) String orderBy,
                @RequestParam(name = "$top", required = false) Integer top,
                @RequestParam(name = "$skip", required = false) Integer skip,
                @RequestParam(name = "$count", defaultValue = "false") boolean count) {
            var result = searchHandler.handle(new GridifyCategoriesQuery(filter, orderBy, top, skip, true));
            return ODataCollection.of(result.data(), count, result.pagedResult().rowCount());
        }

        @GetMapping("/{key:.+}")
        public CategoryViewModel byKey(@PathVariable String key) {
            var id = UUID.fromString(key.replace("(", "").replace(")", ""));
            return getByIdHandler.handle(new GetCategoryByIdQuery(id)).data();
        }
    }
}
