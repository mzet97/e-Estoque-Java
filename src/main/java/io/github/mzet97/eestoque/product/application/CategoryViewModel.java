package io.github.mzet97.eestoque.product.application;

import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.product.domain.Category;

/** Contrato JSON idêntico ao CategoryViewModel .NET (camelCase, sem isDeleted). */
public record CategoryViewModel(
        UUID id,
        String name,
        String description,
        String shortDescription,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {

    public static CategoryViewModel from(Category category) {
        return new CategoryViewModel(category.id(), category.name(), category.description(),
                category.shortDescription(), category.createdAt(), category.updatedAt(), category.deletedAt());
    }
}
