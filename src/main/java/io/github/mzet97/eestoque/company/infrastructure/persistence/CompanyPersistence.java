package io.github.mzet97.eestoque.company.infrastructure.persistence;

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

import io.github.mzet97.eestoque.company.domain.Address;
import io.github.mzet97.eestoque.company.domain.Company;
import io.github.mzet97.eestoque.company.domain.CompanyCriteria;
import io.github.mzet97.eestoque.company.domain.CompanyRepository;
import io.github.mzet97.eestoque.shared.application.PagedResult;
import io.github.mzet97.eestoque.shared.application.SearchResult;
import io.github.mzet97.eestoque.shared.infrastructure.persistence.EntityFieldMaps;
import io.github.mzet97.eestoque.shared.infrastructure.persistence.SpecificationSearch;

interface CompanySpringDataRepository
        extends JpaRepository<CompanyJpaEntity, UUID>, JpaSpecificationExecutor<CompanyJpaEntity> {
}

@Repository
@Transactional(readOnly = true)
class JpaCompanyRepository implements CompanyRepository {

    private static final Map<String, String> ORDER_MAP = Map.of(
            "Name", "name",
            "DocId", "docId",
            "Email", "email",
            "Description", "description",
            "PhoneNumber", "phoneNumber",
            "CreatedAt", "createdAt",
            "UpdatedAt", "updatedAt",
            "DeletedAt", "deletedAt");

    private final CompanySpringDataRepository jpa;

    public JpaCompanyRepository(CompanySpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public Company save(Company company) {
        jpa.save(CompanyMapper.toEntity(company));
        return company;
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return jpa.findById(id).map(CompanyMapper::toDomain);
    }

    @Override
    public SearchResult<Company> search(CompanyCriteria c) {
        Pageable pageable = SpecificationSearch.pageable(c.order(), ORDER_MAP, c.page(), c.size());

        Specification<CompanyJpaEntity> spec = SpecificationSearch.withFilters((cb, root, predicates) -> {
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, c.name(), "name");
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, c.docId(), "docId");
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, c.email(), "email");
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, c.description(), "description");
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, c.phoneNumber(), "phoneNumber");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.id(), "id");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.createdAt(), "createdAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.updatedAt(), "updatedAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.deletedAt(), "deletedAt");
        });

        var page = jpa.findAll(spec, pageable);
        List<Company> data = page.getContent().stream().map(CompanyMapper::toDomain).toList();
        return new SearchResult<>(data, PagedResult.create(c.page(), c.size(), (int) page.getTotalElements()));
    }

    @Override
    public SearchResult<Company> searchGridify(io.github.mzet97.eestoque.shared.application.GridifyCriteria c) {
        return SpecificationSearch.gridifyPage(jpa, EntityFieldMaps.company(), c, items ->
                items.stream().map(CompanyMapper::toDomain).toList());
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}

final class CompanyMapper {

    private CompanyMapper() {
    }

    static Company toDomain(CompanyJpaEntity e) {
        var address = new Address(e.getAddressStreet(), e.getAddressNumber(), e.getAddressComplement(),
                e.getAddressNeighborhood(), e.getAddressDistrict(), e.getAddressCity(), e.getAddressCountry(),
                e.getAddressZipCode(), e.getAddressLatitude(), e.getAddressLongitude());
        return Company.rehydrate(e.getId(), e.getName(), e.getDocId(), e.getEmail(), e.getDescription(),
                e.getPhoneNumber(), address, e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt(), e.isDeleted());
    }

    static CompanyJpaEntity toEntity(Company c) {
        var a = c.address();
        return new CompanyJpaEntity(c.id(), c.name(), c.docId(), c.email(), c.description(), c.phoneNumber(),
                a.street(), a.number(), a.complement(), a.neighborhood(), a.district(), a.city(), a.country(),
                a.zipCode(), a.latitude(), a.longitude(), c.createdAt(), c.updatedAt(), c.deletedAt(), c.isDeleted());
    }
}
