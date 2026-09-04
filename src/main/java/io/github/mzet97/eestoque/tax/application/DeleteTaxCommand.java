package io.github.mzet97.eestoque.tax.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-TAX-003 — hard delete. */
public record DeleteTaxCommand(UUID id) implements Command<Void> {
}
