package io.github.mzet97.eestoque.inventory.domain;

import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/** Payload idêntico ao InventoryCreated do .NET. */
public record InventoryCreated(UUID id, Integer quantity, Instant dateOrder, UUID idProduct)
        implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
