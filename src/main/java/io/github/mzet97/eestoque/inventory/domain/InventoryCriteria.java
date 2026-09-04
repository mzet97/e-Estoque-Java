package io.github.mzet97.eestoque.inventory.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Critérios de busca de inventário. O .NET binda DateOrder como DateTime mas
 * a coluna é varchar — compará-la derruba a query no EF; aqui o filtro é
 * comparado como texto (ASSUMPTIONS A-19).
 */
public record InventoryCriteria(
        UUID id,
        Integer quantity,
        String dateOrder,
        UUID idProduct,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        int page,
        int size) {
}
