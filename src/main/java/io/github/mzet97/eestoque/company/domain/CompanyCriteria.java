package io.github.mzet97.eestoque.company.domain;

import java.time.Instant;
import java.util.UUID;

/** Critérios de busca de empresas (igualdade exata, parity .NET). */
public record CompanyCriteria(
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
