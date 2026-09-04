package io.github.mzet97.eestoque.product.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface CategorySpringDataRepository
        extends JpaRepository<CategoryJpaEntity, UUID>, JpaSpecificationExecutor<CategoryJpaEntity> {
}
