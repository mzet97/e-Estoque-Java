package io.github.mzet97.eestoque.inventory.application;

import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** FR-INV-001. */
public record CreateInventoryCommand(
        @Positive Integer quantity,
        Instant dateOrder,
        @NotNull UUID idProduct) implements Command<UUID> {
}
