package io.github.mzet97.eestoque.customer.application;

import java.util.UUID;

import io.github.mzet97.eestoque.customer.domain.Address;
import io.github.mzet97.eestoque.shared.application.Command;
import jakarta.validation.constraints.Size;

/** FR-CUSTOMER-001. */
public record CreateCustomerCommand(
        @Size(min = 3, max = 80) String name,
        @Size(min = 3, max = 80) String docId,
        @Size(min = 3, max = 250) String email,
        @Size(min = 3, max = 250) String description,
        @Size(min = 3, max = 80) String phoneNumber,
        Address customerAddress) implements Command<UUID> {
}
