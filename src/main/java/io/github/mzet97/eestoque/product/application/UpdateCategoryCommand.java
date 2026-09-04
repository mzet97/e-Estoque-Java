package io.github.mzet97.eestoque.product.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-CAT-002. O id da rota prevalece sobre o corpo (MD-01). */
public record UpdateCategoryCommand(
        UUID id,
        String name,
        String description,
        String shortDescription) implements Command<UUID> {
}
