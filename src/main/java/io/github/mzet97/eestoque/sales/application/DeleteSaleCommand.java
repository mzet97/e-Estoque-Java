package io.github.mzet97.eestoque.sales.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-SALE-003 — soft delete. */
public record DeleteSaleCommand(UUID id) implements Command<Void> {
}
