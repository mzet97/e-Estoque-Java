package io.github.mzet97.eestoque.inventory.application;

import java.time.Instant;
import java.util.UUID;

/** Contrato JSON idêntico ao InventoryViewModel .NET. */
public record InventoryViewModel(
        UUID id,
        Integer quantity,
        Instant dateOrder,
        UUID idProduct,
        ProductInfo product,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {
}
