package io.github.mzet97.eestoque.sales.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Venda com referências carregadas (Customer + Products) para o
 * SaleViewModel. Snapshots de leitura das tabelas Customers/Products.
 */
public record SaleViewData(Sale sale, CustomerSnapshot customer, java.util.List<ProductSnapshot> products) {

    public record CustomerSnapshot(UUID id, String name, String docId, String email, String description,
                                   String phoneNumber, AddressSnapshot customerAddress,
                                   Instant createdAt, Instant updatedAt, Instant deletedAt) {

        public record AddressSnapshot(String street, String number, String complement, String neighborhood,
                                      String district, String city, String country, String zipCode,
                                      String latitude, String longitude) {
        }
    }

    public record ProductSnapshot(UUID id, String name, String description, String shortDescription,
                                  java.math.BigDecimal price, java.math.BigDecimal weight,
                                  java.math.BigDecimal height, java.math.BigDecimal length, String image,
                                  UUID idCategory, CategorySnapshot category, UUID idCompany, CompanySnapshot company,
                                  Instant createdAt, Instant updatedAt, Instant deletedAt) {

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
}
