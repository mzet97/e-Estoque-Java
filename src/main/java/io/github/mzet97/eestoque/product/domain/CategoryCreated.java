package io.github.mzet97.eestoque.product.domain;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/** Payload idêntico ao CategoryCreated do .NET (wire-format camelCase). */
public record CategoryCreated(UUID id, String name, String description, String shortDescription)
        implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
