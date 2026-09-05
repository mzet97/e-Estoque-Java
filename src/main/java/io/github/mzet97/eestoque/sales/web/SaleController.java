package io.github.mzet97.eestoque.sales.web;

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

import io.github.mzet97.eestoque.sales.application.CreateSaleCommand;
import io.github.mzet97.eestoque.sales.application.DeleteSaleCommand;
import io.github.mzet97.eestoque.sales.application.GetSaleByIdQuery;
import io.github.mzet97.eestoque.sales.application.GridifySalesHandler;
import io.github.mzet97.eestoque.sales.application.GridifySalesQuery;
import io.github.mzet97.eestoque.sales.application.SaleHandlers.CreateSaleHandler;
import io.github.mzet97.eestoque.sales.application.SaleHandlers.DeleteSaleHandler;
import io.github.mzet97.eestoque.sales.application.SaleHandlers.GetSaleByIdHandler;
import io.github.mzet97.eestoque.sales.application.SaleHandlers.SearchSalesHandler;
import io.github.mzet97.eestoque.sales.application.SaleHandlers.UpdateSaleHandler;
import io.github.mzet97.eestoque.sales.application.SaleViewModel;
import io.github.mzet97.eestoque.sales.application.SearchSalesQuery;
import io.github.mzet97.eestoque.sales.application.UpdateSaleCommand;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import jakarta.validation.Valid;

/** FR-SALE-001..006. */
@RestController
@RequestMapping("/api/Sales")
public class SaleController {

    private final SearchSalesHandler searchHandler;
    private final GridifySalesHandler gridifyHandler;
    private final GetSaleByIdHandler getByIdHandler;
    private final CreateSaleHandler createHandler;
    private final UpdateSaleHandler updateHandler;
    private final DeleteSaleHandler deleteHandler;

    public SaleController(SearchSalesHandler searchHandler, GridifySalesHandler gridifyHandler,
                          GetSaleByIdHandler getByIdHandler, CreateSaleHandler createHandler,
                          UpdateSaleHandler updateHandler, DeleteSaleHandler deleteHandler) {
        this.searchHandler = searchHandler;
        this.gridifyHandler = gridifyHandler;
        this.getByIdHandler = getByIdHandler;
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.deleteHandler = deleteHandler;
    }

    @GetMapping
    public BaseResultList<SaleViewModel> search(
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) BigDecimal totalPrice,
            @RequestParam(required = false) BigDecimal totalTax,
            @RequestParam(required = false) String saleType,
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) Instant deliveryDate,
            @RequestParam(required = false) Instant saleDate,
            @RequestParam(required = false) Instant paymentDate,
            @RequestParam(required = false) UUID idCustomer,
            @RequestParam(required = false) UUID idProduct,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) Instant createdAt,
            @RequestParam(required = false) Instant updatedAt,
            @RequestParam(required = false) Instant deletedAt,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Integer pageIndex,
            @RequestParam(required = false) Integer pageSize) {
        return searchHandler.handle(new SearchSalesQuery(quantity, totalPrice, totalTax, saleType, paymentType,
                deliveryDate, saleDate, paymentDate, idCustomer, idProduct, id, createdAt, updatedAt, deletedAt,
                order, pageIndex, pageSize));
    }

    @GetMapping("/gridify")
    public BaseResultList<SaleViewModel> gridify(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return gridifyHandler.handle(new GridifySalesQuery(filter, orderBy, page, pageSize, false));
    }

    @GetMapping("/{id}")
    public BaseResult<SaleViewModel> getById(@PathVariable UUID id) {
        return getByIdHandler.handle(new GetSaleByIdQuery(id));
    }

    @PostMapping
    public BaseResult<SaleViewModel> create(@Valid @RequestBody CreateSaleCommand command) {
        var id = createHandler.handle(command);
        return getByIdHandler.handle(new GetSaleByIdQuery(id));
    }

    @PutMapping("/{id}")
    public BaseResult<SaleViewModel> update(@PathVariable UUID id, @Valid @RequestBody UpdateSaleCommand command) {
        var updatedId = updateHandler.handle(new UpdateSaleCommand(id, command.quantity(), command.totalPrice(),
                command.totalTax(), command.saleType(), command.paymentType(), command.deliveryDate(),
                command.saleDate(), command.paymentDate(), command.idCustomer(), command.idsProducts()));
        return getByIdHandler.handle(new GetSaleByIdQuery(updatedId));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        deleteHandler.handle(new DeleteSaleCommand(id));
        return Map.of();
    }
}
