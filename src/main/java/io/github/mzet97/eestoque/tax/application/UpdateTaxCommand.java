package io.github.mzet97.eestoque.tax.application;

import java.math.BigDecimal;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** FR-TAX-002 — id da rota prevalece (MD-01). */
public record UpdateTaxCommand(
        UUID id,
        @Size(min = 3, max = 80) String name,
        @Size(min = 3, max = 250) String description,
        BigDecimal percentage,
        @NotNull UUID idCategory) implements Command<UUID> {
}
