package io.github.mzet97.eestoque.tax.application;

import io.github.mzet97.eestoque.tax.domain.TaxViewData;

/** Mapeamento Tax(+Category) → TaxViewModel. */
final class TaxViews {

    private TaxViews() {
    }

    static TaxViewModel toViewModel(TaxViewData viewData) {
        var tax = viewData.tax();
        var category = viewData.category();
        TaxViewModel.CategoryInfo categoryInfo = null;
        if (category != null) {
            categoryInfo = new TaxViewModel.CategoryInfo(category.id(), category.name(), category.description(),
                    category.shortDescription(), category.createdAt(), category.updatedAt(), category.deletedAt());
        }
        return new TaxViewModel(tax.id(), tax.name(), tax.description(), tax.percentage(), tax.idCategory(),
                categoryInfo, tax.createdAt(), tax.updatedAt(), tax.deletedAt());
    }
}
