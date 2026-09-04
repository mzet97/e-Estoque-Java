package io.github.mzet97.eestoque.product.application;

import java.time.Instant;
import java.util.UUID;

/**
 * Informação de categoria embutida no ProductViewModel. Estrutura
 * (e JSON) idênticos ao CategoryViewModel .NET.
 */
public record CategoryInfo(
        UUID id,
        String name,
        String description,
        String shortDescription,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {
}
