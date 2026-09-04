package io.github.mzet97.eestoque.company.domain;

import java.util.Optional;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.SearchResult;

/** Porta de persistência de empresas. Delete = hard delete (parity). */
public interface CompanyRepository {

    Company save(Company company);

    Optional<Company> findById(UUID id);

    SearchResult<Company> search(CompanyCriteria criteria);

    SearchResult<Company> searchGridify(io.github.mzet97.eestoque.shared.application.GridifyCriteria criteria);

    void deleteById(UUID id);
}
