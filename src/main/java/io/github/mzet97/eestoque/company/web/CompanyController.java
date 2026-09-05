package io.github.mzet97.eestoque.company.web;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.mzet97.eestoque.company.application.CompanyHandlers.CreateCompanyHandler;
import io.github.mzet97.eestoque.company.application.CompanyHandlers.DeleteCompanyHandler;
import io.github.mzet97.eestoque.company.application.CompanyHandlers.GetCompanyByIdHandler;
import io.github.mzet97.eestoque.company.application.CompanyHandlers.SearchCompaniesHandler;
import io.github.mzet97.eestoque.company.application.CompanyHandlers.UpdateCompanyHandler;
import io.github.mzet97.eestoque.company.application.CompanyViewModel;
import io.github.mzet97.eestoque.company.application.CreateCompanyCommand;
import io.github.mzet97.eestoque.company.application.DeleteCompanyCommand;
import io.github.mzet97.eestoque.company.application.GetCompanyByIdQuery;
import io.github.mzet97.eestoque.company.application.GridifyCompaniesHandler;
import io.github.mzet97.eestoque.company.application.GridifyCompaniesQuery;
import io.github.mzet97.eestoque.company.application.SearchCompaniesQuery;
import io.github.mzet97.eestoque.company.application.UpdateCompanyCommand;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;

/** FR-COMPANY-001..006 (POST/PUT/DELETE exigem ROLE_Create — SecurityConfiguration). */
@RestController
@RequestMapping("/api/Companies")
public class CompanyController {

    private final SearchCompaniesHandler searchHandler;
    private final GridifyCompaniesHandler gridifyHandler;
    private final GetCompanyByIdHandler getByIdHandler;
    private final CreateCompanyHandler createHandler;
    private final UpdateCompanyHandler updateHandler;
    private final DeleteCompanyHandler deleteHandler;

    public CompanyController(SearchCompaniesHandler searchHandler, GridifyCompaniesHandler gridifyHandler,
                             GetCompanyByIdHandler getByIdHandler, CreateCompanyHandler createHandler,
                             UpdateCompanyHandler updateHandler, DeleteCompanyHandler deleteHandler) {
        this.searchHandler = searchHandler;
        this.gridifyHandler = gridifyHandler;
        this.getByIdHandler = getByIdHandler;
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.deleteHandler = deleteHandler;
    }

    @GetMapping
    public BaseResultList<CompanyViewModel> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String docId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) Instant createdAt,
            @RequestParam(required = false) Instant updatedAt,
            @RequestParam(required = false) Instant deletedAt,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Integer pageIndex,
            @RequestParam(required = false) Integer pageSize) {
        return searchHandler.handle(new SearchCompaniesQuery(name, docId, email, description, phoneNumber, id,
                createdAt, updatedAt, deletedAt, order, pageIndex, pageSize));
    }

    @GetMapping("/gridify")
    public BaseResultList<CompanyViewModel> gridify(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return gridifyHandler.handle(new GridifyCompaniesQuery(filter, orderBy, page, pageSize, false));
    }

    @GetMapping("/{id}")
    public BaseResult<CompanyViewModel> getById(@PathVariable UUID id) {
        return getByIdHandler.handle(new GetCompanyByIdQuery(id));
    }

    @PostMapping
    public BaseResult<CompanyViewModel> create(@Valid @RequestBody CreateCompanyCommand command) {
        var id = createHandler.handle(command);
        return getByIdHandler.handle(new GetCompanyByIdQuery(id));
    }

    @PutMapping("/{id}")
    public BaseResult<CompanyViewModel> update(@PathVariable UUID id,
                                               @Valid @RequestBody UpdateCompanyCommand command) {
        var updatedId = updateHandler.handle(new UpdateCompanyCommand(id, command.name(), command.docId(),
                command.email(), command.description(), command.phoneNumber(), command.companyAddress()));
        return getByIdHandler.handle(new GetCompanyByIdQuery(updatedId));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        deleteHandler.handle(new DeleteCompanyCommand(id));
        return Map.of();
    }
}
