package io.github.mzet97.eestoque.company.domain;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/** Payload idêntico ao CompanyCreated do .NET. */
public record CompanyCreated(UUID id, String name, String docId, String email, String description,
                             String phoneNumber, Address companyAddress) implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
