package io.github.mzet97.eestoque.product.infrastructure.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.product.domain.Category;
import io.github.mzet97.eestoque.product.domain.CategoryCriteria;
import io.github.mzet97.eestoque.product.domain.CategoryRepository;
import io.github.mzet97.eestoque.shared.application.PagedResult;
import io.github.mzet97.eestoque.shared.application.SearchResult;
import io.github.mzet97.eestoque.shared.infrastructure.persistence.EntityFieldMaps;
import io.github.mzet97.eestoque.shared.infrastructure.persistence.SpecificationSearch;

@Repository
@Transactional(readOnly = true)
public class JpaCategoryRepository implements CategoryRepository {

    private static final Map<String, String> ORDER_MAP = Map.of(
            "Name", "name",
            "Description", "description",
            "ShortDescription", "shortDescription",
            "CreatedAt", "createdAt",
            "UpdatedAt", "updatedAt",
            "DeletedAt", "deletedAt");

    private final CategorySpringDataRepository jpa;

    public JpaCategoryRepository(CategorySpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public Category save(Category category) {
        jpa.save(CategoryMapper.toEntity(category));
        return category;
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpa.findById(id).map(CategoryMapper::toDomain);
    }

    @Override
    public SearchResult<Category> search(CategoryCriteria criteria) {
        Pageable pageable = SpecificationSearch.pageable(criteria.order(), ORDER_MAP, criteria.page(), criteria.size());

        Specification<CategoryJpaEntity> spec = SpecificationSearch.withFilters((cb, root, predicates) -> {
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, criteria.name(), "name");
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, criteria.description(), "description");
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, criteria.shortDescription(), "shortDescription");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, criteria.id(), "id");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, criteria.createdAt(), "createdAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, criteria.updatedAt(), "updatedAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, criteria.deletedAt(), "deletedAt");
        });

        var page = jpa.findAll(spec, pageable);
        List<Category> data = page.getContent().stream().map(CategoryMapper::toDomain).toList();
        return new SearchResult<>(data, PagedResult.create(criteria.page(), criteria.size(), (int) page.getTotalElements()));
    }

    @Override
    public SearchResult<Category> searchGridify(io.github.mzet97.eestoque.shared.application.GridifyCriteria c) {
        return SpecificationSearch.gridifyPage(jpa, EntityFieldMaps.category(), c, items ->
                items.stream().map(CategoryMapper::toDomain).toList());
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    static Sort sort(String order) {
        return SpecificationSearch.sortFrom(order, ORDER_MAP);
    }
}
