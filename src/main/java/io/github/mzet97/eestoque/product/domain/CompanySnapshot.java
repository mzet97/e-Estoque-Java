package io.github.mzet97.eestoque.product.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Snapshot de leitura de Company para composição do ProductViewModel.
 * Mantém o módulo product desacoplado do módulo company (mesma tabela
 * public."Companies", leitura somente leitura).
 */
public record CompanySnapshot(
        UUID id,
        String name,
        String docId,
        String email,
        String description,
        String phoneNumber,
        AddressSnapshot address,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {

    public record AddressSnapshot(String street, String number, String complement, String neighborhood,
                                  String district, String city, String country, String zipCode, String latitude,
                                  String longitude) {
    }
}
