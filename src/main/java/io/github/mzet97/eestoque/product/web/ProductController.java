package io.github.mzet97.eestoque.product.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.mzet97.eestoque.product.application.CreateProductCommand;
import io.github.mzet97.eestoque.product.application.DeleteProductCommand;
import io.github.mzet97.eestoque.product.application.GetProductByIdQuery;
import io.github.mzet97.eestoque.product.application.GridifyHandlers.GridifyProductsHandler;
import io.github.mzet97.eestoque.product.application.ProductHandlers.CreateProductHandler;
import io.github.mzet97.eestoque.product.application.ProductHandlers.DeleteProductHandler;
import io.github.mzet97.eestoque.product.application.ProductHandlers.GetProductByIdHandler;
import io.github.mzet97.eestoque.product.application.ProductHandlers.SearchProductsHandler;
import io.github.mzet97.eestoque.product.application.ProductHandlers.UpdateProductHandler;
import io.github.mzet97.eestoque.product.application.ProductViewModel;
import io.github.mzet97.eestoque.product.application.SearchProductsQuery;
import io.github.mzet97.eestoque.product.application.UpdateProductCommand;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import jakarta.validation.Valid;

/** FR-PROD-001..006. */
@RestController
@RequestMapping("/api/Products")
public class ProductController {

    private final SearchProductsHandler searchHandler;
    private final GridifyProductsHandler gridifyHandler;
    private final GetProductByIdHandler getByIdHandler;
    private final CreateProductHandler createHandler;
    private final UpdateProductHandler updateHandler;
    private final DeleteProductHandler deleteHandler;

    public ProductController(SearchProductsHandler searchHandler, GridifyProductsHandler gridifyHandler,
                             GetProductByIdHandler getByIdHandler, CreateProductHandler createHandler,
                             UpdateProductHandler updateHandler, DeleteProductHandler deleteHandler) {
        this.searchHandler = searchHandler;
        this.gridifyHandler = gridifyHandler;
        this.getByIdHandler = getByIdHandler;
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.deleteHandler = deleteHandler;
    }

    @GetMapping
    public BaseResultList<ProductViewModel> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String shortDescription,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) BigDecimal weight,
            @RequestParam(required = false) BigDecimal height,
            @RequestParam(required = false) BigDecimal length,
            @RequestParam(required = false) UUID idCategory,
            @RequestParam(required = false) UUID idCompany,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) Instant createdAt,
            @RequestParam(required = false) Instant updatedAt,
            @RequestParam(required = false) Instant deletedAt,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Integer pageIndex,
            @RequestParam(required = false) Integer pageSize) {
        return searchHandler.handle(new SearchProductsQuery(name, description, shortDescription, price, weight,
                height, length, idCategory, idCompany, id, createdAt, updatedAt, deletedAt, order, pageIndex,
                pageSize));
    }

    @GetMapping("/gridify")
    public BaseResultList<ProductViewModel> gridify(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return gridifyHandler.handle(new io.github.mzet97.eestoque.product.application.GridifyProductsQuery(
                filter, orderBy, page, pageSize, false));
    }

    @GetMapping("/{id}")
    public BaseResult<ProductViewModel> getById(@PathVariable UUID id) {
        return getByIdHandler.handle(new GetProductByIdQuery(id));
    }

    @PostMapping
    public BaseResult<ProductViewModel> create(@Valid @RequestBody CreateProductCommand command) {
        var id = createHandler.handle(command);
        return getByIdHandler.handle(new GetProductByIdQuery(id));
    }

    @PutMapping("/{id}")
    public BaseResult<ProductViewModel> update(@PathVariable UUID id,
                                               @Valid @RequestBody UpdateProductCommand command) {
        var updatedId = updateHandler.handle(new UpdateProductCommand(id, command.name(), command.description(),
                command.shortDescription(), command.price(), command.weight(), command.height(), command.length(),
                command.image(), command.idCategory(), command.idCompany()));
        return getByIdHandler.handle(new GetProductByIdQuery(updatedId));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        deleteHandler.handle(new DeleteProductCommand(id));
        return Map.of();
    }
}
