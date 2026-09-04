package io.github.mzet97.eestoque.inventory.application;

import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-INV-001. */
public record CreateInventoryCommand(
        Integer quantity,
        Instant dateOrder,
        UUID idProduct) implements Command<UUID> {
}
