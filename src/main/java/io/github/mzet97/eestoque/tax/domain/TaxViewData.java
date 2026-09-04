package io.github.mzet97.eestoque.tax.domain;

import java.time.Instant;
import java.util.UUID;

/** Imposto com a Category carregada (include do .NET). */
public record TaxViewData(Tax tax, CategorySnapshot category) {

    public record CategorySnapshot(UUID id, String name, String description, String shortDescription,
                                   Instant createdAt, Instant updatedAt, Instant deletedAt) {
    }
}
