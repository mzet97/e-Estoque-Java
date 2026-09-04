package io.github.mzet97.eestoque.product.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.SearchResult;

/** Porta de persistência de produtos. Delete = hard delete (parity). */
public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID id);

    /** Lê o produto com Category e Company (includes do .NET). */
    Optional<ProductViewData> findDetailedById(UUID id);

    SearchResult<ProductViewData> search(ProductCriteria criteria);

    SearchResult<ProductViewData> searchGridify(io.github.mzet97.eestoque.shared.application.GridifyCriteria criteria);

    void deleteById(UUID id);
}
