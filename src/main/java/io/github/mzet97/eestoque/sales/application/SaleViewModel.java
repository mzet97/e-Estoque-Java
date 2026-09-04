package io.github.mzet97.eestoque.sales.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.github.mzet97.eestoque.sales.domain.PaymentType;
import io.github.mzet97.eestoque.sales.domain.SaleType;

/** Contrato JSON idêntico ao SaleViewModel .NET (enums como int). */
public record SaleViewModel(
        UUID id,
        Integer quantity,
        BigDecimal totalPrice,
        BigDecimal totalTax,
        SaleType saleType,
        PaymentType paymentType,
        Instant deliveryDate,
        Instant saleDate,
        Instant paymentDate,
        UUID idCustomer,
        CustomerInfo customer,
        List<ProductInfo> products,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {

    public record CustomerInfo(UUID id, String name, String docId, String email, String description,
                               String phoneNumber, CustomerAddressInfo customerAddress,
                               Instant createdAt, Instant updatedAt, Instant deletedAt) {

        public record CustomerAddressInfo(String street, String number, String complement, String neighborhood,
                                          String district, String city, String country, String zipCode,
                                          String latitude, String longitude) {
        }
    }

    public record ProductInfo(UUID id, String name, String description, String shortDescription,
                              BigDecimal price, BigDecimal weight, BigDecimal height, BigDecimal length,
                              String image, UUID idCategory, CategoryInfo category, UUID idCompany,
                              CompanyInfo company, Instant createdAt, Instant updatedAt, Instant deletedAt) {

        public record CategoryInfo(UUID id, String name, String description, String shortDescription,
                                   Instant createdAt, Instant updatedAt, Instant deletedAt) {
        }

        public record CompanyInfo(UUID id, String name, String docId, String email, String description,
                                  String phoneNumber, CompanyAddressInfo companyAddress,
                                  Instant createdAt, Instant updatedAt, Instant deletedAt) {

            public record CompanyAddressInfo(String street, String number, String complement, String neighborhood,
                                             String district, String city, String country, String zipCode,
                                             String latitude, String longitude) {
            }
        }
    }
}
