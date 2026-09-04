package io.github.mzet97.eestoque.customer.application;

import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.customer.domain.CustomerCriteria;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.PagedSearch;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-CUSTOMER-005. */
public record SearchCustomersQuery(
        String name,
        String docId,
        String email,
        String description,
        String phoneNumber,
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        Integer pageIndex,
        Integer pageSize) implements Query<BaseResultList<CustomerViewModel>>, PagedSearch {

    public CustomerCriteria toCriteria() {
        return new CustomerCriteria(id, name, docId, email, description, phoneNumber,
                createdAt, updatedAt, deletedAt, order, pageOrDefault(), sizeOrDefault());
    }
}
