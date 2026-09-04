package io.github.mzet97.eestoque.inventory.web;

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

import io.github.mzet97.eestoque.inventory.application.CreateInventoryCommand;
import io.github.mzet97.eestoque.inventory.application.DeleteInventoryCommand;
import io.github.mzet97.eestoque.inventory.application.GetInventoryByIdQuery;
import io.github.mzet97.eestoque.inventory.application.InventoryViewModel;
import io.github.mzet97.eestoque.inventory.application.SearchInventoriesQuery;
import io.github.mzet97.eestoque.inventory.application.UpdateInventoryCommand;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.CommandBus;
import io.github.mzet97.eestoque.shared.application.QueryBus;
import jakarta.validation.Valid;

/** FR-INV-001..006. */
@RestController
@RequestMapping("/api/Inventories")
public class InventoryController {

    private final CommandBus commands;
    private final QueryBus queries;

    public InventoryController(CommandBus commands, QueryBus queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @GetMapping
    public BaseResultList<InventoryViewModel> search(
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) String dateOrder,
            @RequestParam(required = false) UUID idProduct,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) Instant createdAt,
            @RequestParam(required = false) Instant updatedAt,
            @RequestParam(required = false) Instant deletedAt,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Integer pageIndex,
            @RequestParam(required = false) Integer pageSize) {
        return queries.dispatch(new SearchInventoriesQuery(quantity, dateOrder, idProduct, id,
                createdAt, updatedAt, deletedAt, order, pageIndex, pageSize));
    }

    @GetMapping("/{id}")
    public BaseResult<InventoryViewModel> getById(@PathVariable UUID id) {
        return queries.dispatch(new GetInventoryByIdQuery(id));
    }

    @PostMapping
    public BaseResult<InventoryViewModel> create(@Valid @RequestBody CreateInventoryCommand command) {
        var id = commands.dispatch(command);
        return queries.dispatch(new GetInventoryByIdQuery(id));
    }

    @PutMapping("/{id}")
    public BaseResult<InventoryViewModel> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateInventoryCommand command) {
        var updatedId = commands.dispatch(new UpdateInventoryCommand(id, command.quantity(), command.dateOrder(),
                command.idProduct()));
        return queries.dispatch(new GetInventoryByIdQuery(updatedId));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        commands.dispatch(new DeleteInventoryCommand(id));
        return Map.of();
    }
}
