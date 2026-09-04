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

import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.CommandBus;
import io.github.mzet97.eestoque.shared.application.QueryBus;
import io.github.mzet97.eestoque.sales.application.CreateSaleCommand;
import io.github.mzet97.eestoque.sales.application.DeleteSaleCommand;
import io.github.mzet97.eestoque.sales.application.GetSaleByIdQuery;
import io.github.mzet97.eestoque.sales.application.SaleViewModel;
import io.github.mzet97.eestoque.sales.application.SearchSalesQuery;
import io.github.mzet97.eestoque.sales.application.UpdateSaleCommand;
import jakarta.validation.Valid;

/** FR-SALE-001..006. */
@RestController
@RequestMapping("/api/Sales")
public class SaleController {

    private final CommandBus commands;
    private final QueryBus queries;

    public SaleController(CommandBus commands, QueryBus queries) {
        this.commands = commands;
        this.queries = queries;
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
        return queries.dispatch(new SearchSalesQuery(quantity, totalPrice, totalTax, saleType, paymentType,
                deliveryDate, saleDate, paymentDate, idCustomer, idProduct, id, createdAt, updatedAt, deletedAt,
                order, pageIndex, pageSize));
    }

    @GetMapping("/{id}")
    public BaseResult<SaleViewModel> getById(@PathVariable UUID id) {
        return queries.dispatch(new GetSaleByIdQuery(id));
    }

    @PostMapping
    public BaseResult<SaleViewModel> create(@Valid @RequestBody CreateSaleCommand command) {
        var id = commands.dispatch(command);
        return queries.dispatch(new GetSaleByIdQuery(id));
    }

    @PutMapping("/{id}")
    public BaseResult<SaleViewModel> update(@PathVariable UUID id, @Valid @RequestBody UpdateSaleCommand command) {
        var updatedId = commands.dispatch(new UpdateSaleCommand(id, command.quantity(), command.totalPrice(),
                command.totalTax(), command.saleType(), command.paymentType(), command.deliveryDate(),
                command.saleDate(), command.paymentDate(), command.idCustomer(), command.idsProducts()));
        return queries.dispatch(new GetSaleByIdQuery(updatedId));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        commands.dispatch(new DeleteSaleCommand(id));
        return Map.of();
    }
}
