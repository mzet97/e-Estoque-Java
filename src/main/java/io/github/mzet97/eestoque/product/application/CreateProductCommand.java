package io.github.mzet97.eestoque.product.application;

import java.math.BigDecimal;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** FR-PROD-001. */
public record CreateProductCommand(
        @Size(min = 3, max = 250) String name,
        @Size(min = 3, max = 500) String description,
        @Size(min = 3, max = 250) String shortDescription,
        BigDecimal price,
        BigDecimal weight,
        BigDecimal height,
        BigDecimal length,
        @Size(min = 3, max = 5000) String image,
        @NotNull UUID idCategory,
        @NotNull UUID idCompany) implements Command<UUID> {
}
