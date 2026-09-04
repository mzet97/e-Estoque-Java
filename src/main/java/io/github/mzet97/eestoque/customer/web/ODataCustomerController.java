package io.github.mzet97.eestoque.customer.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.mzet97.eestoque.customer.application.CustomerViewModel;
import io.github.mzet97.eestoque.customer.application.GetCustomerByIdQuery;
import io.github.mzet97.eestoque.customer.application.GridifyCustomersQuery;
import io.github.mzet97.eestoque.shared.application.QueryBus;
import io.github.mzet97.eestoque.shared.infrastructure.web.query.ODataCollection;
import io.github.mzet97.eestoque.shared.infrastructure.web.query.ODataRequestParser;

/** FR-ODATA-003: /odata/Customers. */
@RestController
@RequestMapping("/odata/Customers")
public class ODataCustomerController {

    private final QueryBus queries;

    public ODataCustomerController(QueryBus queries) {
        this.queries = queries;
    }

    @GetMapping
    public Map<String, Object> list(
            @RequestParam(name = "$filter", required = false) String filter,
            @RequestParam(name = "$orderby", required = false) String orderBy,
            @RequestParam(name = "$top", required = false) Integer top,
            @RequestParam(name = "$skip", required = false) Integer skip,
            @RequestParam(name = "$count", defaultValue = "false") boolean count) {
        var criteria = ODataRequestParser.toCriteria(filter, orderBy, top, skip);
        var result = queries.dispatch(new GridifyCustomersQuery(filter, orderBy, top, skip, true));
        return ODataCollection.of(result.data(), count, result.pagedResult().rowCount());
    }

    @GetMapping("/{key:.+}")
    public CustomerViewModel byKey(@PathVariable String key) {
        var id = UUID.fromString(key.replace("(", "").replace(")", ""));
        return queries.dispatch(new GetCustomerByIdQuery(id)).data();
    }
}
