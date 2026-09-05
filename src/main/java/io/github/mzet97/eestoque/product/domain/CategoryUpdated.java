package io.github.mzet97.eestoque.product.domain;

import java.util.UUID;

import org.springframework.modulith.events.Externalized;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/** Payload idêntico ao CategoryUpdated do .NET. */
@Externalized("category-service::category-updated")
public record CategoryUpdated(UUID id, String name, String description, String shortDescription)
        implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
