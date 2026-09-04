package io.github.mzet97.eestoque.tax.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Critérios de busca de impostos (igualdade exata, parity .NET). */
public record TaxCriteria(
        UUID id,
        String name,
        String description,
        BigDecimal percentage,
        UUID idCategory,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        int page,
        int size) {
}
