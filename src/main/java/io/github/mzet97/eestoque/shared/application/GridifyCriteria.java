package io.github.mzet97.eestoque.shared.application;

/**
 * Critérios da busca gridify (FR-*-006): filter em sintaxe Gridify,
 * orderBy "campo asc|desc", paginação 1-based.
 */
public record GridifyCriteria(FilterNode filter, String orderBy, int page, int size) {
}
