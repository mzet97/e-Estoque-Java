package io.github.mzet97.eestoque.product.application;

import java.math.BigDecimal;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-PROD-001. */
public record CreateProductCommand(
        String name,
        String description,
        String shortDescription,
        BigDecimal price,
        BigDecimal weight,
        BigDecimal height,
        BigDecimal length,
        String image,
        UUID idCategory,
        UUID idCompany) implements Command<UUID> {
}
