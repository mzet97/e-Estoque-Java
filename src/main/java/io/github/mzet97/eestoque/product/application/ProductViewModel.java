package io.github.mzet97.eestoque.product.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Contrato JSON idêntico ao ProductViewModel .NET. */
public record ProductViewModel(
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
        CategoryInfo category,
        UUID idCompany,
        CompanyInfo company,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {
}
