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

import io.github.mzet97.eestoque.company.application.CompanyViewModel;
import io.github.mzet97.eestoque.company.application.CreateCompanyCommand;
import io.github.mzet97.eestoque.company.application.DeleteCompanyCommand;
import io.github.mzet97.eestoque.company.application.GetCompanyByIdQuery;
import io.github.mzet97.eestoque.company.application.SearchCompaniesQuery;
import io.github.mzet97.eestoque.company.application.UpdateCompanyCommand;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.CommandBus;
import io.github.mzet97.eestoque.shared.application.QueryBus;

/** FR-COMPANY-001..006 (POST/PUT/DELETE exigem ROLE_Create — SecurityConfiguration). */
@RestController
@RequestMapping("/api/Companies")
public class CompanyController {

    private final CommandBus commands;
    private final QueryBus queries;

    public CompanyController(CommandBus commands, QueryBus queries) {
        this.commands = commands;
        this.queries = queries;
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
        return queries.dispatch(new SearchCompaniesQuery(name, docId, email, description, phoneNumber, id,
                createdAt, updatedAt, deletedAt, order, pageIndex, pageSize));
    }

    @GetMapping("/gridify")
    public BaseResultList<CompanyViewModel> gridify(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return queries.dispatch(new io.github.mzet97.eestoque.company.application.GridifyCompaniesQuery(
                filter, orderBy, page, pageSize));
    }

    @GetMapping("/{id}")
    public BaseResult<CompanyViewModel> getById(@PathVariable UUID id) {
        return queries.dispatch(new GetCompanyByIdQuery(id));
    }

    @PostMapping
    public BaseResult<CompanyViewModel> create(@Valid @RequestBody CreateCompanyCommand command) {
        var id = commands.dispatch(command);
        return queries.dispatch(new GetCompanyByIdQuery(id));
    }

    @PutMapping("/{id}")
    public BaseResult<CompanyViewModel> update(@PathVariable UUID id,
                                               @Valid @RequestBody UpdateCompanyCommand command) {
        var updatedId = commands.dispatch(new UpdateCompanyCommand(id, command.name(), command.docId(),
                command.email(), command.description(), command.phoneNumber(), command.companyAddress()));
        return queries.dispatch(new GetCompanyByIdQuery(updatedId));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        commands.dispatch(new DeleteCompanyCommand(id));
        return Map.of();
    }
}
