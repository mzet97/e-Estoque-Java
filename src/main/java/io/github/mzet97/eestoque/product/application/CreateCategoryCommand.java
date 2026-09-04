package io.github.mzet97.eestoque.product.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-CAT-001. Bean Validation cobre limites de entrada; invariantes completas no domínio. */
public record CreateCategoryCommand(
        String name,
        String description,
        String shortDescription) implements Command<UUID> {
}
