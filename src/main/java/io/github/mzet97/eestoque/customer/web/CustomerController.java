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

import io.github.mzet97.eestoque.customer.application.CreateCustomerCommand;
import io.github.mzet97.eestoque.customer.application.CustomerHandlers.CreateCustomerHandler;
import io.github.mzet97.eestoque.customer.application.CustomerHandlers.DeleteCustomerHandler;
import io.github.mzet97.eestoque.customer.application.CustomerHandlers.GetCustomerByIdHandler;
import io.github.mzet97.eestoque.customer.application.CustomerHandlers.SearchCustomersHandler;
import io.github.mzet97.eestoque.customer.application.CustomerHandlers.UpdateCustomerHandler;
import io.github.mzet97.eestoque.customer.application.CustomerViewModel;
import io.github.mzet97.eestoque.customer.application.DeleteCustomerCommand;
import io.github.mzet97.eestoque.customer.application.GetCustomerByIdQuery;
import io.github.mzet97.eestoque.customer.application.GridifyCustomersHandler;
import io.github.mzet97.eestoque.customer.application.GridifyCustomersQuery;
import io.github.mzet97.eestoque.customer.application.SearchCustomersQuery;
import io.github.mzet97.eestoque.customer.application.UpdateCustomerCommand;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import jakarta.validation.Valid;

/** FR-CUSTOMER-001..006. */
@RestController
@RequestMapping("/api/Customers")
public class CustomerController {

    private final SearchCustomersHandler searchHandler;
    private final GridifyCustomersHandler gridifyHandler;
    private final GetCustomerByIdHandler getByIdHandler;
    private final CreateCustomerHandler createHandler;
    private final UpdateCustomerHandler updateHandler;
    private final DeleteCustomerHandler deleteHandler;

    public CustomerController(SearchCustomersHandler searchHandler, GridifyCustomersHandler gridifyHandler,
                              GetCustomerByIdHandler getByIdHandler, CreateCustomerHandler createHandler,
                              UpdateCustomerHandler updateHandler, DeleteCustomerHandler deleteHandler) {
        this.searchHandler = searchHandler;
        this.gridifyHandler = gridifyHandler;
        this.getByIdHandler = getByIdHandler;
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.deleteHandler = deleteHandler;
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
        return searchHandler.handle(new SearchCustomersQuery(name, docId, email, description, phoneNumber, id,
                createdAt, updatedAt, deletedAt, order, pageIndex, pageSize));
    }

    @GetMapping("/gridify")
    public BaseResultList<CustomerViewModel> gridify(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return gridifyHandler.handle(new GridifyCustomersQuery(filter, orderBy, page, pageSize, false));
    }

    @GetMapping("/{id}")
    public BaseResult<CustomerViewModel> getById(@PathVariable UUID id) {
        return getByIdHandler.handle(new GetCustomerByIdQuery(id));
    }

    @PostMapping
    public BaseResult<CustomerViewModel> create(@Valid @RequestBody CreateCustomerCommand command) {
        var id = createHandler.handle(command);
        return getByIdHandler.handle(new GetCustomerByIdQuery(id));
    }

    @PutMapping("/{id}")
    public BaseResult<CustomerViewModel> update(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateCustomerCommand command) {
        var updatedId = updateHandler.handle(new UpdateCustomerCommand(id, command.name(), command.docId(),
                command.email(), command.description(), command.phoneNumber(), command.customerAddress()));
        return getByIdHandler.handle(new GetCustomerByIdQuery(updatedId));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        deleteHandler.handle(new DeleteCustomerCommand(id));
        return Map.of();
    }
}
