package io.github.mzet97.eestoque.tax.domain;

import java.math.BigDecimal;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/** Payload idêntico ao TaxCreated do .NET. */
public record TaxCreated(UUID id, String name, String description, BigDecimal percentage, UUID idCategory)
        implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
