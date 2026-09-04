package io.github.mzet97.eestoque.customer.domain;

import java.time.Instant;
import java.util.UUID;

/** Critérios de busca de clientes (igualdade exata, parity .NET). */
public record CustomerCriteria(
        UUID id,
        String name,
        String docId,
        String email,
        String description,
        String phoneNumber,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        int page,
        int size) {
}
