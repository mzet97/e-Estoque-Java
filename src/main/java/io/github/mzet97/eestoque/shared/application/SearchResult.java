package io.github.mzet97.eestoque.shared.application;

import java.util.List;

/** Resultado de busca paginada da camada de persistência. */
public record SearchResult<T>(List<T> data, PagedResult pagedResult) {
}
