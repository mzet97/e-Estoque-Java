package io.github.mzet97.eestoque.company.application;

import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.company.domain.Address;
import io.github.mzet97.eestoque.company.domain.Company;

/** Contrato JSON idêntico ao CompanyViewModel .NET. */
public record CompanyViewModel(
        UUID id,
        String name,
        String docId,
        String email,
        String description,
        String phoneNumber,
        Address companyAddress,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {

    public static CompanyViewModel from(Company company) {
        return new CompanyViewModel(company.id(), company.name(), company.docId(), company.email(),
                company.description(), company.phoneNumber(), company.address(),
                company.createdAt(), company.updatedAt(), company.deletedAt());
    }
}
