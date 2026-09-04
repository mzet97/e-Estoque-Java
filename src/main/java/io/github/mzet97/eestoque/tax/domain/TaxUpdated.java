package io.github.mzet97.eestoque.tax.domain;

import java.math.BigDecimal;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/** Payload idêntico ao TaxUpdated do .NET. */
public record TaxUpdated(UUID id, String name, String description, BigDecimal percentage, UUID idCategory)
        implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
