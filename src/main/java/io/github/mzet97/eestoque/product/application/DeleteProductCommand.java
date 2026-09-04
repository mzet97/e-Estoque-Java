package io.github.mzet97.eestoque.product.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-PROD-003 — hard delete. */
public record DeleteProductCommand(UUID id) implements Command<Void> {
}
