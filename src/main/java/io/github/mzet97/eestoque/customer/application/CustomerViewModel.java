package io.github.mzet97.eestoque.customer.application;

import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.customer.domain.Address;
import io.github.mzet97.eestoque.customer.domain.Customer;

/** Contrato JSON idêntico ao CustomerViewModel .NET. */
public record CustomerViewModel(
        UUID id,
        String name,
        String docId,
        String email,
        String description,
        String phoneNumber,
        Address customerAddress,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {

    public static CustomerViewModel from(Customer customer) {
        return new CustomerViewModel(customer.id(), customer.name(), customer.docId(), customer.email(),
                customer.description(), customer.phoneNumber(), customer.address(),
                customer.createdAt(), customer.updatedAt(), customer.deletedAt());
    }
}
