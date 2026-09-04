package io.github.mzet97.eestoque.sales.domain;

import java.util.Optional;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.SearchResult;

/**
 * Porta de persistência de vendas. Delete = soft delete (parity com
 * DisableAsync do .NET).
 */
public interface SaleRepository {

    Sale save(Sale sale);

    Optional<Sale> findById(UUID id);

    /** Lê a venda com Customer, SaleProducts e Products (includes do .NET). */
    Optional<SaleViewData> findDetailedById(UUID id);

    SearchResult<SaleViewData> search(SaleCriteria criteria);

    SearchResult<SaleViewData> searchGridify(io.github.mzet97.eestoque.shared.application.GridifyCriteria criteria);

    /** Soft delete: IsDeleted=true + DeletedAt. */
    void disable(UUID id, java.time.Instant deletedAt);
}
