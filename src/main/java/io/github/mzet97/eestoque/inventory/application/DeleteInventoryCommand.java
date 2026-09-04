package io.github.mzet97.eestoque.inventory.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-INV-003 — hard delete. */
public record DeleteInventoryCommand(UUID id) implements Command<Void> {
}
