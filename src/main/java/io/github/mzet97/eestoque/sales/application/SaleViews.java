package io.github.mzet97.eestoque.sales.application;

import io.github.mzet97.eestoque.sales.domain.SaleViewData;

/** Mapeamento Sale(+Customer,+Products) → SaleViewModel. */
final class SaleViews {

    private SaleViews() {
    }

    static SaleViewModel toViewModel(SaleViewData viewData) {
        var sale = viewData.sale();
        var customer = viewData.customer() == null ? null : toCustomerInfo(viewData.customer());
        var products = viewData.products() == null ? java.util.List.<SaleViewModel.ProductInfo>of()
                : viewData.products().stream().map(SaleViews::toProductInfo).toList();
        return new SaleViewModel(sale.id(), sale.quantity(), sale.totalPrice(), sale.totalTax(), sale.saleType(),
                sale.paymentType(), sale.deliveryDate(), sale.saleDate(), sale.paymentDate(), sale.idCustomer(),
                customer, products, sale.createdAt(), sale.updatedAt(), sale.deletedAt());
    }

    private static SaleViewModel.CustomerInfo toCustomerInfo(SaleViewData.CustomerSnapshot s) {
        SaleViewModel.CustomerInfo.CustomerAddressInfo address = null;
        if (s.customerAddress() != null) {
            var a = s.customerAddress();
            address = new SaleViewModel.CustomerInfo.CustomerAddressInfo(a.street(), a.number(), a.complement(),
                    a.neighborhood(), a.district(), a.city(), a.country(), a.zipCode(), a.latitude(), a.longitude());
        }
        return new SaleViewModel.CustomerInfo(s.id(), s.name(), s.docId(), s.email(), s.description(),
                s.phoneNumber(), address, s.createdAt(), s.updatedAt(), s.deletedAt());
    }

    private static SaleViewModel.ProductInfo toProductInfo(SaleViewData.ProductSnapshot s) {
        SaleViewModel.ProductInfo.CategoryInfo category = null;
        if (s.category() != null) {
            var c = s.category();
            category = new SaleViewModel.ProductInfo.CategoryInfo(c.id(), c.name(), c.description(),
                    c.shortDescription(), c.createdAt(), c.updatedAt(), c.deletedAt());
        }
        SaleViewModel.ProductInfo.CompanyInfo company = null;
        if (s.company() != null) {
            var c = s.company();
            SaleViewModel.ProductInfo.CompanyInfo.CompanyAddressInfo address = null;
            if (c.companyAddress() != null) {
                var a = c.companyAddress();
                address = new SaleViewModel.ProductInfo.CompanyInfo.CompanyAddressInfo(a.street(), a.number(),
                        a.complement(), a.neighborhood(), a.district(), a.city(), a.country(), a.zipCode(),
                        a.latitude(), a.longitude());
            }
            company = new SaleViewModel.ProductInfo.CompanyInfo(c.id(), c.name(), c.docId(), c.email(),
                    c.description(), c.phoneNumber(), address, c.createdAt(), c.updatedAt(), c.deletedAt());
        }
        return new SaleViewModel.ProductInfo(s.id(), s.name(), s.description(), s.shortDescription(), s.price(),
                s.weight(), s.height(), s.length(), s.image(), s.idCategory(), category, s.idCompany(), company,
                s.createdAt(), s.updatedAt(), s.deletedAt());
    }
}
