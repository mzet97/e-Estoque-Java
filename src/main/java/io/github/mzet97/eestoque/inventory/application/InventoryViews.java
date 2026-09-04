package io.github.mzet97.eestoque.inventory.application;

import io.github.mzet97.eestoque.inventory.domain.InventoryViewData;
import io.github.mzet97.eestoque.inventory.domain.ProductSnapshot;

/** Mapeamento Inventory(+Product) → InventoryViewModel. */
final class InventoryViews {

    private InventoryViews() {
    }

    static InventoryViewModel toViewModel(InventoryViewData viewData) {
        var inventory = viewData.inventory();
        var product = viewData.product() == null ? null : toProductInfo(viewData.product());
        return new InventoryViewModel(inventory.id(), inventory.quantity(), inventory.dateOrder(),
                inventory.idProduct(), product, inventory.createdAt(), inventory.updatedAt(), inventory.deletedAt());
    }

    static ProductInfo toProductInfo(ProductSnapshot s) {
        var category = s.category() == null ? null : new ProductInfo.CategoryRef(s.category().id(),
                s.category().name(), s.category().description(), s.category().shortDescription(),
                s.category().createdAt(), s.category().updatedAt(), s.category().deletedAt());
        ProductInfo.CompanyRef company = null;
        if (s.company() != null) {
            var c = s.company();
            ProductInfo.CompanyRef.AddressRef address = null;
            if (c.companyAddress() != null) {
                var a = c.companyAddress();
                address = new ProductInfo.CompanyRef.AddressRef(a.street(), a.number(), a.complement(),
                        a.neighborhood(), a.district(), a.city(), a.country(), a.zipCode(), a.latitude(),
                        a.longitude());
            }
            company = new ProductInfo.CompanyRef(c.id(), c.name(), c.docId(), c.email(), c.description(),
                    c.phoneNumber(), address, c.createdAt(), c.updatedAt(), c.deletedAt());
        }
        return new ProductInfo(s.id(), s.name(), s.description(), s.shortDescription(), s.price(), s.weight(),
                s.height(), s.length(), s.image(), s.idCategory(), category, s.idCompany(), company,
                s.createdAt(), s.updatedAt(), s.deletedAt());
    }
}
