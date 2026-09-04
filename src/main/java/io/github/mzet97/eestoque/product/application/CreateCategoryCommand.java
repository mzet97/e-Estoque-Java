package io.github.mzet97.eestoque.product.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;
import jakarta.validation.constraints.Size;

/** FR-CAT-001. Bean Validation cobre limites de entrada; invariantes completas no domínio. */
public record CreateCategoryCommand(
        @Size(min = 3, max = 80) String name,
        @Size(min = 3, max = 5000) String description,
        @Size(min = 3, max = 500) String shortDescription) implements Command<UUID> {
}
