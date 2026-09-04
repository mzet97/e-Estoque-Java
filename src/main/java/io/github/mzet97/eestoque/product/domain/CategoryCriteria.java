package io.github.mzet97.eestoque.product.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Critérios de busca de categorias (igualdade exata, como no .NET).
 * Campos nulos nunca filtram.
 */
public record CategoryCriteria(
        UUID id,
        String name,
        String description,
        String shortDescription,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        int page,
        int size) {
}
