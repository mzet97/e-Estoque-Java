package io.github.mzet97.eestoque.inventory.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.mzet97.eestoque.inventory.application.GetInventoryByIdQuery;
import io.github.mzet97.eestoque.inventory.application.GridifyInventoriesHandler;
import io.github.mzet97.eestoque.inventory.application.GridifyInventoriesQuery;
import io.github.mzet97.eestoque.inventory.application.InventoryHandlers.GetInventoryByIdHandler;
import io.github.mzet97.eestoque.inventory.application.InventoryViewModel;
import io.github.mzet97.eestoque.shared.application.query.ODataRequestParser;
import io.github.mzet97.eestoque.shared.infrastructure.web.query.ODataCollection;

/** FR-ODATA-004: /odata/Inventories. */
@RestController
@RequestMapping("/odata/Inventories")
public class ODataInventoryController {

    private final GridifyInventoriesHandler searchHandler;
    private final GetInventoryByIdHandler getByIdHandler;

    public ODataInventoryController(GridifyInventoriesHandler searchHandler,
                                    GetInventoryByIdHandler getByIdHandler) {
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
        var criteria = ODataRequestParser.toCriteria(filter, orderBy, top, skip);
        var result = searchHandler.handle(new GridifyInventoriesQuery(filter, orderBy, top, skip, true));
        return ODataCollection.of(result.data(), count, result.pagedResult().rowCount());
    }

    @GetMapping("/{key:.+}")
    public InventoryViewModel byKey(@PathVariable String key) {
        var id = UUID.fromString(key.replace("(", "").replace(")", ""));
        return getByIdHandler.handle(new GetInventoryByIdQuery(id)).data();
    }
}
