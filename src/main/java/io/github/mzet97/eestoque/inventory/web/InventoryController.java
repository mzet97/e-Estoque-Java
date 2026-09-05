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
import io.github.mzet97.eestoque.inventory.application.GridifyInventoriesHandler;
import io.github.mzet97.eestoque.inventory.application.GridifyInventoriesQuery;
import io.github.mzet97.eestoque.inventory.application.InventoryHandlers.CreateInventoryHandler;
import io.github.mzet97.eestoque.inventory.application.InventoryHandlers.DeleteInventoryHandler;
import io.github.mzet97.eestoque.inventory.application.InventoryHandlers.GetInventoryByIdHandler;
import io.github.mzet97.eestoque.inventory.application.InventoryHandlers.SearchInventoriesHandler;
import io.github.mzet97.eestoque.inventory.application.InventoryHandlers.UpdateInventoryHandler;
import io.github.mzet97.eestoque.inventory.application.InventoryViewModel;
import io.github.mzet97.eestoque.inventory.application.SearchInventoriesQuery;
import io.github.mzet97.eestoque.inventory.application.UpdateInventoryCommand;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import jakarta.validation.Valid;

/** FR-INV-001..006. */
@RestController
@RequestMapping("/api/Inventories")
public class InventoryController {

    private final SearchInventoriesHandler searchHandler;
    private final GridifyInventoriesHandler gridifyHandler;
    private final GetInventoryByIdHandler getByIdHandler;
    private final CreateInventoryHandler createHandler;
    private final UpdateInventoryHandler updateHandler;
    private final DeleteInventoryHandler deleteHandler;

    public InventoryController(SearchInventoriesHandler searchHandler, GridifyInventoriesHandler gridifyHandler,
                               GetInventoryByIdHandler getByIdHandler, CreateInventoryHandler createHandler,
                               UpdateInventoryHandler updateHandler, DeleteInventoryHandler deleteHandler) {
        this.searchHandler = searchHandler;
        this.gridifyHandler = gridifyHandler;
        this.getByIdHandler = getByIdHandler;
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.deleteHandler = deleteHandler;
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
        return searchHandler.handle(new SearchInventoriesQuery(quantity, dateOrder, idProduct, id,
                createdAt, updatedAt, deletedAt, order, pageIndex, pageSize));
    }

    @GetMapping("/gridify")
    public BaseResultList<InventoryViewModel> gridify(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return gridifyHandler.handle(new GridifyInventoriesQuery(filter, orderBy, page, pageSize, false));
    }

    @GetMapping("/{id}")
    public BaseResult<InventoryViewModel> getById(@PathVariable UUID id) {
        return getByIdHandler.handle(new GetInventoryByIdQuery(id));
    }

    @PostMapping
    public BaseResult<InventoryViewModel> create(@Valid @RequestBody CreateInventoryCommand command) {
        var id = createHandler.handle(command);
        return getByIdHandler.handle(new GetInventoryByIdQuery(id));
    }

    @PutMapping("/{id}")
    public BaseResult<InventoryViewModel> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateInventoryCommand command) {
        var updatedId = updateHandler.handle(new UpdateInventoryCommand(id, command.quantity(), command.dateOrder(),
                command.idProduct()));
        return getByIdHandler.handle(new GetInventoryByIdQuery(updatedId));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        deleteHandler.handle(new DeleteInventoryCommand(id));
        return Map.of();
    }
}
