package io.github.mzet97.eestoque.sales.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import io.github.mzet97.eestoque.sales.domain.PaymentType;
import io.github.mzet97.eestoque.sales.domain.SaleCriteria;
import io.github.mzet97.eestoque.sales.domain.SaleType;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.PagedSearch;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-SALE-005 — saleType/paymentType como string, com defaults do .NET. */
public record SearchSalesQuery(
        Integer quantity,
        BigDecimal totalPrice,
        BigDecimal totalTax,
        String saleType,
        String paymentType,
        Instant deliveryDate,
        Instant saleDate,
        Instant paymentDate,
        UUID idCustomer,
        UUID idProduct,
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        Integer pageIndex,
        Integer pageSize) implements Query<BaseResultList<SaleViewModel>>, PagedSearch {

    public SaleCriteria toCriteria() {
        var saleTypeCode = SaleType.fromStringOrDefault(saleType, SaleType.UNITARY).toInt();
        var paymentTypeCode = PaymentType.fromStringOrDefault(paymentType, PaymentType.PIX).toInt();
        return new SaleCriteria(id, quantity, totalPrice, totalTax, saleTypeCode, paymentTypeCode,
                deliveryDate, saleDate, paymentDate, idCustomer, idProduct, createdAt, updatedAt, deletedAt,
                order, pageOrDefault(), sizeOrDefault());
    }
}
