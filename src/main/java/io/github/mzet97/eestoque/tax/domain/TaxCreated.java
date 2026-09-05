package io.github.mzet97.eestoque.tax.domain;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.modulith.events.Externalized;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/** Payload idêntico ao TaxCreated do .NET. */
@Externalized("tax-service::tax-created")
public record TaxCreated(UUID id, String name, String description, BigDecimal percentage, UUID idCategory)
        implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
