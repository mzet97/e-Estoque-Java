package io.github.mzet97.eestoque.inventory.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Informação de produto embutida no InventoryViewModel (leitura da tabela
 * "Products" por projeção). JSON idêntico ao ProductViewModel .NET.
 */
public record ProductInfo(
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
        CategoryRef category,
        UUID idCompany,
        CompanyRef company,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {

    public record CategoryRef(UUID id, String name, String description, String shortDescription,
                              Instant createdAt, Instant updatedAt, Instant deletedAt) {
    }

    public record CompanyRef(UUID id, String name, String docId, String email, String description,
                             String phoneNumber, AddressRef companyAddress,
                             Instant createdAt, Instant updatedAt, Instant deletedAt) {

        public record AddressRef(String street, String number, String complement, String neighborhood,
                                 String district, String city, String country, String zipCode, String latitude,
                                 String longitude) {
        }
    }
}
