package io.github.mzet97.eestoque.product.application;

import io.github.mzet97.eestoque.product.domain.Category;
import io.github.mzet97.eestoque.product.domain.CompanySnapshot;
import io.github.mzet97.eestoque.product.domain.ProductViewData;

/** Mapeamento explícito Produto(+referências) → ProductViewModel. */
final class ProductViews {

    private ProductViews() {
    }

    static ProductViewModel toViewModel(ProductViewData viewData) {
        var product = viewData.product();
        var category = viewData.category() == null ? null : toCategoryInfo(viewData.category());
        var company = viewData.company() == null ? null : toCompanyInfo(viewData.company());
        return new ProductViewModel(product.id(), product.name(), product.description(), product.shortDescription(),
                product.price(), product.weight(), product.height(), product.length(), product.image(),
                product.idCategory(), category, product.idCompany(), company,
                product.createdAt(), product.updatedAt(), product.deletedAt());
    }

    static CategoryInfo toCategoryInfo(Category category) {
        return new CategoryInfo(category.id(), category.name(), category.description(), category.shortDescription(),
                category.createdAt(), category.updatedAt(), category.deletedAt());
    }

    static CompanyInfo toCompanyInfo(CompanySnapshot snapshot) {
        var a = snapshot.address();
        var address = a == null ? null : new CompanyInfo.AddressInfo(a.street(), a.number(), a.complement(),
                a.neighborhood(), a.district(), a.city(), a.country(), a.zipCode(), a.latitude(), a.longitude());
        return new CompanyInfo(snapshot.id(), snapshot.name(), snapshot.docId(), snapshot.email(),
                snapshot.description(), snapshot.phoneNumber(), address,
                snapshot.createdAt(), snapshot.updatedAt(), snapshot.deletedAt());
    }
}
