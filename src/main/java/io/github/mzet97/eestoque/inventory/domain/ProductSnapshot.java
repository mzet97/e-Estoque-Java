package io.github.mzet97.eestoque.inventory.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Snapshot de leitura do produto (tabela public."Products" + refs) para o
 * InventoryViewModel. Módulo inventory não importa o módulo product.
 */
public record ProductSnapshot(
        UUID id,
        String name,
        String description,
        String shortDescription,
        BigDecimal price,
        BigDecimal weight,
        BigDecimal height,
        BigDecimal length,
        String image,
        UUID idCategory,
        CategorySnapshot category,
        UUID idCompany,
        CompanySnapshot company,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {

    public record CategorySnapshot(UUID id, String name, String description, String shortDescription,
                                   Instant createdAt, Instant updatedAt, Instant deletedAt) {
    }

    public record CompanySnapshot(UUID id, String name, String docId, String email, String description,
                                  String phoneNumber, AddressSnapshot companyAddress,
                                  Instant createdAt, Instant updatedAt, Instant deletedAt) {

        public record AddressSnapshot(String street, String number, String complement, String neighborhood,
                                      String district, String city, String country, String zipCode,
                                      String latitude, String longitude) {
        }
    }
}
