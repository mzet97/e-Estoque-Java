package io.github.mzet97.eestoque.product.application;

import java.time.Instant;
import java.util.UUID;

/**
 * Informação de empresa embutida no ProductViewModel. Estrutura (e JSON)
 * idênticos ao CompanyViewModel .NET.
 */
public record CompanyInfo(
        UUID id,
        String name,
        String docId,
        String email,
        String description,
        String phoneNumber,
        AddressInfo companyAddress,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {

    public record AddressInfo(String street, String number, String complement, String neighborhood, String district,
                              String city, String country, String zipCode, String latitude, String longitude) {
    }
}
