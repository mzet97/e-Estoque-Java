package io.github.mzet97.eestoque.product.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Critérios de busca de produtos (igualdade exata, parity .NET). */
public record ProductCriteria(
        UUID id,
        String name,
        String description,
        String shortDescription,
        BigDecimal price,
        BigDecimal weight,
        BigDecimal height,
        BigDecimal length,
        UUID idCategory,
        UUID idCompany,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        int page,
        int size) {

    /** Zero é o "default" do .NET e nunca filtra (AddFilterIfNotDefault). */
    public BigDecimal priceOrDefault() {
        return price == null || price.signum() == 0 ? null : price;
    }

    public BigDecimal weightOrDefault() {
        return weight == null || weight.signum() == 0 ? null : weight;
    }

    public BigDecimal heightOrDefault() {
        return height == null || height.signum() == 0 ? null : height;
    }

    public BigDecimal lengthOrDefault() {
        return length == null || length.signum() == 0 ? null : length;
    }
}
