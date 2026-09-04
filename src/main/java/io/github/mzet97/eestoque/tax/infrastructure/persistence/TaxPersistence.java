package io.github.mzet97.eestoque.tax.infrastructure.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.shared.application.PagedResult;
import io.github.mzet97.eestoque.shared.application.SearchResult;
import io.github.mzet97.eestoque.shared.infrastructure.persistence.SpecificationSearch;
import io.github.mzet97.eestoque.tax.domain.Tax;
import io.github.mzet97.eestoque.tax.domain.TaxCriteria;
import io.github.mzet97.eestoque.tax.domain.TaxRepository;
import io.github.mzet97.eestoque.tax.domain.TaxViewData;

interface TaxSpringDataRepository
        extends JpaRepository<TaxJpaEntity, UUID>, JpaSpecificationExecutor<TaxJpaEntity> {
}

interface CategoryRefSpringDataRepository extends JpaRepository<CategoryRefJpaEntity, UUID> {
}

@Repository
@Transactional(readOnly = true)
class JpaTaxRepository implements TaxRepository {

    private static final Map<String, String> ORDER_MAP = Map.of(
            "Name", "name",
            "Description", "description",
            "Percentage", "percentage",
            "CreatedAt", "createdAt",
            "UpdatedAt", "updatedAt",
            "DeletedAt", "deletedAt");

    private final TaxSpringDataRepository jpa;
    private final CategoryRefSpringDataRepository categories;

    JpaTaxRepository(TaxSpringDataRepository jpa, CategoryRefSpringDataRepository categories) {
        this.jpa = jpa;
        this.categories = categories;
    }

    @Override
    @Transactional
    public Tax save(Tax tax) {
        var category = categories.getReferenceById(tax.idCategory());
        var entity = jpa.findById(tax.id()).map(existing -> {
            existing.setName(tax.name());
            existing.setDescription(tax.description());
            existing.setPercentage(tax.percentage());
            existing.setCategory(category);
            existing.setUpdatedAt(tax.updatedAt());
            existing.setDeletedAt(tax.deletedAt());
            existing.setDeleted(tax.isDeleted());
            return existing;
        }).orElseGet(() -> new TaxJpaEntity(tax.id(), tax.name(), tax.description(), tax.percentage(),
                tax.idCategory(), category, tax.createdAt(), tax.updatedAt(), tax.deletedAt(), tax.isDeleted()));

        jpa.save(entity);
        return tax;
    }

    @Override
    public Optional<Tax> findById(UUID id) {
        return jpa.findById(id).map(TaxMapper::toDomain);
    }

    @Override
    public Optional<TaxViewData> findDetailedById(UUID id) {
        return jpa.findById(id).map(TaxMapper::toViewData);
    }

    @Override
    public SearchResult<TaxViewData> search(TaxCriteria c) {
        Pageable pageable = SpecificationSearch.pageable(c.order(), ORDER_MAP, c.page(), c.size());

        Specification<TaxJpaEntity> spec = SpecificationSearch.withFilters((cb, root, predicates) -> {
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, c.name(), "name");
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, c.description(), "description");
            if (c.percentage() != null && c.percentage().signum() != 0) {
                predicates.add(cb.equal(root.get("percentage"), c.percentage()));
            }
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.idCategory(), "category");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.id(), "id");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.createdAt(), "createdAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.updatedAt(), "updatedAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.deletedAt(), "deletedAt");
        });

        var page = jpa.findAll(spec, pageable);
        List<TaxViewData> data = page.getContent().stream().map(TaxMapper::toViewData).toList();
        return new SearchResult<>(data, PagedResult.create(c.page(), c.size(), (int) page.getTotalElements()));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}

final class TaxMapper {

    private TaxMapper() {
    }

    static Tax toDomain(TaxJpaEntity e) {
        return Tax.rehydrate(e.getId(), e.getName(), e.getDescription(), e.getPercentage(), e.getIdCategory(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt(), e.isDeleted());
    }

    static TaxViewData toViewData(TaxJpaEntity e) {
        TaxViewData.CategorySnapshot category = null;
        if (e.getCategory() != null) {
            var c = e.getCategory();
            category = new TaxViewData.CategorySnapshot(c.getId(), c.getName(), c.getDescription(),
                    c.getShortDescription(), c.getCreatedAt(), c.getUpdatedAt(), c.getDeletedAt());
        }
        return new TaxViewData(toDomain(e), category);
    }
}
