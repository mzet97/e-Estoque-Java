package io.github.mzet97.eestoque.tax.application;

import java.math.BigDecimal;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-TAX-001. */
public record CreateTaxCommand(
        String name,
        String description,
        BigDecimal percentage,
        UUID idCategory) implements Command<UUID> {
}
