package io.github.mzet97.eestoque.company.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.mzet97.eestoque.company.application.CompanyViewModel;
import io.github.mzet97.eestoque.company.application.CompanyHandlers.GetCompanyByIdHandler;
import io.github.mzet97.eestoque.company.application.GetCompanyByIdQuery;
import io.github.mzet97.eestoque.company.application.GridifyCompaniesHandler;
import io.github.mzet97.eestoque.company.application.GridifyCompaniesQuery;
import io.github.mzet97.eestoque.shared.application.query.ODataRequestParser;
import io.github.mzet97.eestoque.shared.infrastructure.web.query.ODataCollection;

/** FR-ODATA-002: /odata/Companies. */
@RestController
@RequestMapping("/odata/Companies")
public class ODataCompanyController {

    private final GridifyCompaniesHandler searchHandler;
    private final GetCompanyByIdHandler getByIdHandler;

    public ODataCompanyController(GridifyCompaniesHandler searchHandler, GetCompanyByIdHandler getByIdHandler) {
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
        var result = searchHandler.handle(new GridifyCompaniesQuery(filter, orderBy, top, skip, true));
        return ODataCollection.of(result.data(), count, result.pagedResult().rowCount());
    }

    @GetMapping("/{key:.+}")
    public CompanyViewModel byKey(@PathVariable String key) {
        var id = UUID.fromString(key.replace("(", "").replace(")", ""));
        return getByIdHandler.handle(new GetCompanyByIdQuery(id)).data();
    }
}
