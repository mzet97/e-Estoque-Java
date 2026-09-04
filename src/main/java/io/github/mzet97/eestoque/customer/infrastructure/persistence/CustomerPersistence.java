package io.github.mzet97.eestoque.customer.infrastructure.persistence;

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

import io.github.mzet97.eestoque.customer.domain.Address;
import io.github.mzet97.eestoque.customer.domain.Customer;
import io.github.mzet97.eestoque.customer.domain.CustomerCriteria;
import io.github.mzet97.eestoque.customer.domain.CustomerRepository;
import io.github.mzet97.eestoque.shared.application.PagedResult;
import io.github.mzet97.eestoque.shared.application.SearchResult;
import io.github.mzet97.eestoque.shared.infrastructure.persistence.SpecificationSearch;

interface CustomerSpringDataRepository
        extends JpaRepository<CustomerJpaEntity, UUID>, JpaSpecificationExecutor<CustomerJpaEntity> {
}

@Repository
@Transactional(readOnly = true)
class JpaCustomerRepository implements CustomerRepository {

    private static final Map<String, String> ORDER_MAP = Map.of(
            "Name", "name",
            "DocId", "docId",
            "Email", "email",
            "Description", "description",
            "PhoneNumber", "phoneNumber",
            "CreatedAt", "createdAt",
            "UpdatedAt", "updatedAt",
            "DeletedAt", "deletedAt");

    private final CustomerSpringDataRepository jpa;

    public JpaCustomerRepository(CustomerSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        jpa.save(CustomerMapper.toEntity(customer));
        return customer;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpa.findById(id).map(CustomerMapper::toDomain);
    }

    @Override
    public SearchResult<Customer> search(CustomerCriteria c) {
        Pageable pageable = SpecificationSearch.pageable(c.order(), ORDER_MAP, c.page(), c.size());

        Specification<CustomerJpaEntity> spec = SpecificationSearch.withFilters((cb, root, predicates) -> {
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
        List<Customer> data = page.getContent().stream().map(CustomerMapper::toDomain).toList();
        return new SearchResult<>(data, PagedResult.create(c.page(), c.size(), (int) page.getTotalElements()));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}

final class CustomerMapper {

    private CustomerMapper() {
    }

    static Customer toDomain(CustomerJpaEntity e) {
        var address = new Address(e.getAddressStreet(), e.getAddressNumber(), e.getAddressComplement(),
                e.getAddressNeighborhood(), e.getAddressDistrict(), e.getAddressCity(), e.getAddressCountry(),
                e.getAddressZipCode(), e.getAddressLatitude(), e.getAddressLongitude());
        return Customer.rehydrate(e.getId(), e.getName(), e.getDocId(), e.getEmail(), e.getDescription(),
                e.getPhoneNumber(), address, e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt(), e.isDeleted());
    }

    static CustomerJpaEntity toEntity(Customer c) {
        var a = c.address();
        return new CustomerJpaEntity(c.id(), c.name(), c.docId(), c.email(), c.description(), c.phoneNumber(),
                a.street(), a.number(), a.complement(), a.neighborhood(), a.district(), a.city(), a.country(),
                a.zipCode(), a.latitude(), a.longitude(), c.createdAt(), c.updatedAt(), c.deletedAt(), c.isDeleted());
    }
}
