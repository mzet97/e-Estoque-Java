package io.github.mzet97.eestoque.product.domain;

import java.util.Optional;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.GridifyCriteria;
import io.github.mzet97.eestoque.shared.application.SearchResult;

/** Porta de persistência de categorias. Delete = hard delete (parity). */
public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(UUID id);

    SearchResult<Category> search(CategoryCriteria criteria);

    SearchResult<Category> searchGridify(GridifyCriteria criteria);

    /** Hard delete, como RemoveAsync do .NET. */
    void deleteById(UUID id);
}
