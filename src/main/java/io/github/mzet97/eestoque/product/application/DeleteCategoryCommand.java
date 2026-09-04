package io.github.mzet97.eestoque.product.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-CAT-003 — hard delete. */
public record DeleteCategoryCommand(UUID id) implements Command<Void> {
}
