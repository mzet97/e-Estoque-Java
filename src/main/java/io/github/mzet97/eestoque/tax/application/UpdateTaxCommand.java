package io.github.mzet97.eestoque.tax.application;

import java.math.BigDecimal;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-TAX-002 — id da rota prevalece (MD-01). */
public record UpdateTaxCommand(
        UUID id,
        String name,
        String description,
        BigDecimal percentage,
        UUID idCategory) implements Command<UUID> {
}
