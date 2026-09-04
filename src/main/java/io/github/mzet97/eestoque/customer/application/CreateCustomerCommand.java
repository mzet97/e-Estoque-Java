package io.github.mzet97.eestoque.customer.application;

import java.util.UUID;

import io.github.mzet97.eestoque.customer.domain.Address;
import io.github.mzet97.eestoque.shared.application.Command;

/** FR-CUSTOMER-001. */
public record CreateCustomerCommand(
        String name,
        String docId,
        String email,
        String description,
        String phoneNumber,
        Address customerAddress) implements Command<UUID> {
}
