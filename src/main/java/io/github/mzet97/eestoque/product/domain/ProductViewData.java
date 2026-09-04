package io.github.mzet97.eestoque.product.domain;

/**
 * Produto com referências carregadas (equivalente aos Include de
 * Category/Company do .NET) para mapear o ProductViewModel.
 */
public record ProductViewData(Product product, Category category, CompanySnapshot company) {
}
