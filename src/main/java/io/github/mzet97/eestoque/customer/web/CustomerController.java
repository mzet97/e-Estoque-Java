package io.github.mzet97.eestoque.customer.web;

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

import io.github.mzet97.eestoque.customer.application.CustomerViewModel;
import io.github.mzet97.eestoque.customer.application.CreateCustomerCommand;
import io.github.mzet97.eestoque.customer.application.DeleteCustomerCommand;
import io.github.mzet97.eestoque.customer.application.GetCustomerByIdQuery;
import io.github.mzet97.eestoque.customer.application.SearchCustomersQuery;
import io.github.mzet97.eestoque.customer.application.UpdateCustomerCommand;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.CommandBus;
import io.github.mzet97.eestoque.shared.application.QueryBus;
import jakarta.validation.Valid;

/** FR-CUSTOMER-001..006. */
@RestController
@RequestMapping("/api/Customers")
public class CustomerController {

    private final CommandBus commands;
    private final QueryBus queries;

    public CustomerController(CommandBus commands, QueryBus queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @GetMapping
    public BaseResultList<CustomerViewModel> search(
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
        return queries.dispatch(new SearchCustomersQuery(name, docId, email, description, phoneNumber, id,
                createdAt, updatedAt, deletedAt, order, pageIndex, pageSize));
    }

    @GetMapping("/{id}")
    public BaseResult<CustomerViewModel> getById(@PathVariable UUID id) {
        return queries.dispatch(new GetCustomerByIdQuery(id));
    }

    @PostMapping
    public BaseResult<CustomerViewModel> create(@Valid @RequestBody CreateCustomerCommand command) {
        var id = commands.dispatch(command);
        return queries.dispatch(new GetCustomerByIdQuery(id));
    }

    @PutMapping("/{id}")
    public BaseResult<CustomerViewModel> update(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateCustomerCommand command) {
        var updatedId = commands.dispatch(new UpdateCustomerCommand(id, command.name(), command.docId(),
                command.email(), command.description(), command.phoneNumber(), command.customerAddress()));
        return queries.dispatch(new GetCustomerByIdQuery(updatedId));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        commands.dispatch(new DeleteCustomerCommand(id));
        return Map.of();
    }
}
