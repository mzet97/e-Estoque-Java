package io.github.mzet97.eestoque.tax.web;

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
import io.github.mzet97.eestoque.tax.application.CreateTaxCommand;
import io.github.mzet97.eestoque.tax.application.DeleteTaxCommand;
import io.github.mzet97.eestoque.tax.application.GetTaxByIdQuery;
import io.github.mzet97.eestoque.tax.application.SearchTaxesQuery;
import io.github.mzet97.eestoque.tax.application.TaxViewModel;
import io.github.mzet97.eestoque.tax.application.UpdateTaxCommand;
import jakarta.validation.Valid;

/** FR-TAX-001..006. Rota preservada "Taxs" (plural do original). */
@RestController
@RequestMapping("/api/Taxs")
public class TaxController {

    private final CommandBus commands;
    private final QueryBus queries;

    public TaxController(CommandBus commands, QueryBus queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @GetMapping
    public BaseResultList<TaxViewModel> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal percentage,
            @RequestParam(required = false) UUID idCategory,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) Instant createdAt,
            @RequestParam(required = false) Instant updatedAt,
            @RequestParam(required = false) Instant deletedAt,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Integer pageIndex,
            @RequestParam(required = false) Integer pageSize) {
        return queries.dispatch(new SearchTaxesQuery(name, description, percentage, idCategory, id,
                createdAt, updatedAt, deletedAt, order, pageIndex, pageSize));
    }

    @GetMapping("/{id}")
    public BaseResult<TaxViewModel> getById(@PathVariable UUID id) {
        return queries.dispatch(new GetTaxByIdQuery(id));
    }

    @PostMapping
    public BaseResult<TaxViewModel> create(@Valid @RequestBody CreateTaxCommand command) {
        var id = commands.dispatch(command);
        return queries.dispatch(new GetTaxByIdQuery(id));
    }

    @PutMapping("/{id}")
    public BaseResult<TaxViewModel> update(@PathVariable UUID id, @Valid @RequestBody UpdateTaxCommand command) {
        var updatedId = commands.dispatch(new UpdateTaxCommand(id, command.name(), command.description(),
                command.percentage(), command.idCategory()));
        return queries.dispatch(new GetTaxByIdQuery(updatedId));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        commands.dispatch(new DeleteTaxCommand(id));
        return Map.of();
    }
}
