package io.github.mzet97.eestoque.customer.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-CUSTOMER-003 — hard delete. */
public record DeleteCustomerCommand(UUID id) implements Command<Void> {
}
