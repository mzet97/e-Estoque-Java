package io.github.mzet97.eestoque.sales.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.mzet97.eestoque.sales.application.GetSaleByIdQuery;
import io.github.mzet97.eestoque.sales.application.GridifySalesHandler;
import io.github.mzet97.eestoque.sales.application.GridifySalesQuery;
import io.github.mzet97.eestoque.sales.application.SaleHandlers.GetSaleByIdHandler;
import io.github.mzet97.eestoque.sales.application.SaleViewModel;
import io.github.mzet97.eestoque.shared.application.query.ODataRequestParser;
import io.github.mzet97.eestoque.shared.infrastructure.web.query.ODataCollection;

/** FR-ODATA-006: /odata/Sales. */
@RestController
@RequestMapping("/odata/Sales")
public class ODataSaleController {

    private final GridifySalesHandler searchHandler;
    private final GetSaleByIdHandler getByIdHandler;

    public ODataSaleController(GridifySalesHandler searchHandler, GetSaleByIdHandler getByIdHandler) {
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
        var result = searchHandler.handle(new GridifySalesQuery(filter, orderBy, top, skip, true));
        return ODataCollection.of(result.data(), count, result.pagedResult().rowCount());
    }

    @GetMapping("/{key:.+}")
    public SaleViewModel byKey(@PathVariable String key) {
        var id = UUID.fromString(key.replace("(", "").replace(")", ""));
        return getByIdHandler.handle(new GetSaleByIdQuery(id)).data();
    }
}
