package io.github.mzet97.eestoque.customer.domain;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/** Payload idêntico ao CustomerUpdated do .NET. */
public record CustomerUpdated(UUID id, String name, String docId, String email, String description,
                              String phoneNumber, Address customerAddress) implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
