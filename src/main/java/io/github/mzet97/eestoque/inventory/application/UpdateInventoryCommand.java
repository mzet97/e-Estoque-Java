package io.github.mzet97.eestoque.inventory.application;

import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-INV-002 — id da rota prevalece (MD-01). */
public record UpdateInventoryCommand(
        UUID id,
        Integer quantity,
        Instant dateOrder,
        UUID idProduct) implements Command<UUID> {
}
