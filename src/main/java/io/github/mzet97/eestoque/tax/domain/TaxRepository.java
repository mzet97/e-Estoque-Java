package io.github.mzet97.eestoque.tax.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.SearchResult;

/** Porta de persistência de impostos. Delete = hard delete (parity). */
public interface TaxRepository {

    Tax save(Tax tax);

    Optional<Tax> findById(UUID id);

    /** Lê o imposto com a Category (include do .NET). */
    Optional<TaxViewData> findDetailedById(UUID id);

    SearchResult<TaxViewData> search(TaxCriteria criteria);

    void deleteById(UUID id);
}
