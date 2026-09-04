package io.github.mzet97.eestoque.product.infrastructure.persistence;

import io.github.mzet97.eestoque.product.domain.Category;

final class CategoryMapper {

    private CategoryMapper() {
    }

    static Category toDomain(CategoryJpaEntity entity) {
        return Category.rehydrate(entity.getId(), entity.getName(), entity.getDescription(),
                entity.getShortDescription(), entity.getCreatedAt(), entity.getUpdatedAt(),
                entity.getDeletedAt(), entity.isDeleted());
    }

    static CategoryJpaEntity toEntity(Category category) {
        return new CategoryJpaEntity(category.id(), category.name(), category.description(),
                category.shortDescription(), category.createdAt(), category.updatedAt(),
                category.deletedAt(), category.isDeleted());
    }
}
