package io.github.mzet97.eestoque.tax.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Contrato JSON idêntico ao TaxViewModel .NET. */
public record TaxViewModel(
        UUID id,
        String name,
        String description,
        BigDecimal percentage,
        UUID idCategory,
        CategoryInfo category,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {

    public record CategoryInfo(UUID id, String name, String description, String shortDescription,
                               Instant createdAt, Instant updatedAt, Instant deletedAt) {
    }
}
