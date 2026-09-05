package io.github.mzet97.eestoque.tax.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.mzet97.eestoque.shared.application.query.ODataRequestParser;
import io.github.mzet97.eestoque.shared.infrastructure.web.query.ODataCollection;
import io.github.mzet97.eestoque.tax.application.GetTaxByIdQuery;
import io.github.mzet97.eestoque.tax.application.GridifyTaxesHandler;
import io.github.mzet97.eestoque.tax.application.GridifyTaxesQuery;
import io.github.mzet97.eestoque.tax.application.TaxHandlers.GetTaxByIdHandler;
import io.github.mzet97.eestoque.tax.application.TaxViewModel;

/** FR-ODATA-007: /odata/Taxs. */
@RestController
@RequestMapping("/odata/Taxs")
public class ODataTaxController {

    private final GridifyTaxesHandler searchHandler;
    private final GetTaxByIdHandler getByIdHandler;

    public ODataTaxController(GridifyTaxesHandler searchHandler, GetTaxByIdHandler getByIdHandler) {
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
        var result = searchHandler.handle(new GridifyTaxesQuery(filter, orderBy, top, skip, true));
        return ODataCollection.of(result.data(), count, result.pagedResult().rowCount());
    }

    @GetMapping("/{key:.+}")
    public TaxViewModel byKey(@PathVariable String key) {
        var id = UUID.fromString(key.replace("(", "").replace(")", ""));
        return getByIdHandler.handle(new GetTaxByIdQuery(id)).data();
    }
}
