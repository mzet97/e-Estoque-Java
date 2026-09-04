package io.github.mzet97.eestoque.product.infrastructure.persistence;

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

import io.github.mzet97.eestoque.product.domain.Category;
import io.github.mzet97.eestoque.product.domain.CompanySnapshot;
import io.github.mzet97.eestoque.product.domain.CompanyReader;
import io.github.mzet97.eestoque.product.domain.Product;
import io.github.mzet97.eestoque.product.domain.ProductCriteria;
import io.github.mzet97.eestoque.product.domain.ProductRepository;
import io.github.mzet97.eestoque.product.domain.ProductViewData;
import io.github.mzet97.eestoque.shared.application.PagedResult;
import io.github.mzet97.eestoque.shared.application.SearchResult;
import io.github.mzet97.eestoque.shared.infrastructure.persistence.SpecificationSearch;

interface ProductSpringDataRepository
        extends JpaRepository<ProductJpaEntity, UUID>, JpaSpecificationExecutor<ProductJpaEntity> {
}

interface CompanySummarySpringDataRepository extends JpaRepository<CompanySummaryJpaEntity, UUID> {
}

@Repository
@Transactional(readOnly = true)
class JpaProductRepository implements ProductRepository {

    private static final Map<String, String> ORDER_MAP = Map.of(
            "Name", "name",
            "Description", "description",
            "ShortDescription", "shortDescription",
            "Price", "price",
            "Weight", "weight",
            "Height", "height",
            "Length", "length",
            "CreatedAt", "createdAt",
            "UpdatedAt", "updatedAt",
            "DeletedAt", "deletedAt");

    private final ProductSpringDataRepository jpa;
    private final CategorySpringDataRepository categories;
    private final CompanySummarySpringDataRepository companies;

    JpaProductRepository(ProductSpringDataRepository jpa, CategorySpringDataRepository categories,
                         CompanySummarySpringDataRepository companies) {
        this.jpa = jpa;
        this.categories = categories;
        this.companies = companies;
    }

    @Override
    @Transactional
    public Product save(Product product) {
        // Referências já validadas pelo handler; proxies bastam para as FKs.
        var category = categories.getReferenceById(product.idCategory());
        var company = companies.getReferenceById(product.idCompany());

        var entity = jpa.findById(product.id()).map(existing -> {
            existing.setName(product.name());
            existing.setDescription(product.description());
            existing.setShortDescription(product.shortDescription());
            existing.setPrice(product.price());
            existing.setWeight(product.weight());
            existing.setHeight(product.height());
            existing.setLength(product.length());
            existing.setImage(product.image());
            existing.setCategory(category);
            existing.setCompany(company);
            existing.setUpdatedAt(product.updatedAt());
            existing.setDeletedAt(product.deletedAt());
            existing.setDeleted(product.isDeleted());
            return existing;
        }).orElseGet(() -> new ProductJpaEntity(product.id(), product.name(), product.description(),
                product.shortDescription(), product.price(), product.weight(), product.height(), product.length(),
                product.image(), category, company, product.createdAt(), product.updatedAt(), product.deletedAt(),
                product.isDeleted()));

        jpa.save(entity);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpa.findById(id).map(ProductMapper::toDomain);
    }

    @Override
    public Optional<ProductViewData> findDetailedById(UUID id) {
        return jpa.findById(id).map(ProductMapper::toViewData);
    }

    @Override
    public SearchResult<ProductViewData> search(ProductCriteria c) {
        Pageable pageable = SpecificationSearch.pageable(c.order(), ORDER_MAP, c.page(), c.size());

        Specification<ProductJpaEntity> spec = SpecificationSearch.withFilters((cb, root, predicates) -> {
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, c.name(), "name");
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, c.description(), "description");
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, c.shortDescription(), "shortDescription");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.priceOrDefault(), "price");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.weightOrDefault(), "weight");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.heightOrDefault(), "height");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.lengthOrDefault(), "length");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.idCategory(), "category");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.idCompany(), "company");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.id(), "id");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.createdAt(), "createdAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.updatedAt(), "updatedAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.deletedAt(), "deletedAt");
        });

        var page = jpa.findAll(spec, pageable);
        List<ProductViewData> data = page.getContent().stream().map(ProductMapper::toViewData).toList();
        return new SearchResult<>(data, PagedResult.create(c.page(), c.size(), (int) page.getTotalElements()));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}

@Repository
@Transactional(readOnly = true)
class JpaCompanyReader implements CompanyReader {

    private final CompanySummarySpringDataRepository jpa;

    JpaCompanyReader(CompanySummarySpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existsById(UUID id) {
        return jpa.existsById(id);
    }
}

final class ProductMapper {

    private ProductMapper() {
    }

    static Product toDomain(ProductJpaEntity e) {
        return Product.rehydrate(e.getId(), e.getName(), e.getDescription(), e.getShortDescription(),
                e.getPrice(), e.getWeight(), e.getHeight(), e.getLength(), e.getImage(),
                e.getCategory() != null ? e.getCategory().getId() : null,
                e.getCompany() != null ? e.getCompany().getId() : null,
                e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt(), e.isDeleted());
    }

    static ProductViewData toViewData(ProductJpaEntity e) {
        Category category = null;
        CompanySnapshot company = null;
        if (e.getCategory() != null) {
            var c = e.getCategory();
            category = Category.rehydrate(c.getId(), c.getName(), c.getDescription(), c.getShortDescription(),
                    c.getCreatedAt(), c.getUpdatedAt(), c.getDeletedAt(), c.isDeleted());
        }
        if (e.getCompany() != null) {
            var co = e.getCompany();
            var address = new CompanySnapshot.AddressSnapshot(co.getAddressStreet(), co.getAddressNumber(),
                    co.getAddressComplement(), co.getAddressNeighborhood(), co.getAddressDistrict(),
                    co.getAddressCity(), co.getAddressCountry(), co.getAddressZipCode(), co.getAddressLatitude(),
                    co.getAddressLongitude());
            company = new CompanySnapshot(co.getId(), co.getName(), co.getDocId(), co.getEmail(),
                    co.getDescription(), co.getPhoneNumber(), address, co.getCreatedAt(), co.getUpdatedAt(),
                    co.getDeletedAt());
        }
        return new ProductViewData(toDomain(e), category, company);
    }
}
