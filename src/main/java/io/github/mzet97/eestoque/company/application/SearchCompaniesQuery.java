package io.github.mzet97.eestoque.company.application;

import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.company.domain.CompanyCriteria;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.PagedSearch;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-COMPANY-005. */
public record SearchCompaniesQuery(
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
        Integer pageSize) implements Query<BaseResultList<CompanyViewModel>>, PagedSearch {

    public CompanyCriteria toCriteria() {
        return new CompanyCriteria(id, name, docId, email, description, phoneNumber,
                createdAt, updatedAt, deletedAt, order, pageOrDefault(), sizeOrDefault());
    }
}
