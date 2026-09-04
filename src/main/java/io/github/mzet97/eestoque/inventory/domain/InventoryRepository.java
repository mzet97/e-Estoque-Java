package io.github.mzet97.eestoque.inventory.domain;

import java.util.Optional;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.SearchResult;

/** Porta de persistência de inventário. Delete = hard delete (parity). */
public interface InventoryRepository {

    Inventory save(Inventory inventory);

    Optional<Inventory> findById(UUID id);

    /** Lê o inventário com o Product (include do .NET). */
    Optional<InventoryViewData> findDetailedById(UUID id);

    SearchResult<InventoryViewData> search(InventoryCriteria criteria);

    SearchResult<InventoryViewData> searchGridify(io.github.mzet97.eestoque.shared.application.GridifyCriteria criteria);

    void deleteById(UUID id);
}
