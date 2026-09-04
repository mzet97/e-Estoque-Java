package io.github.mzet97.eestoque.sales.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Critérios de busca de vendas. saleType/paymentType chegam como string do
 * .NET (helper com default Pix/Unitary) e são convertidos para o código int.
 */
public record SaleCriteria(
        UUID id,
        Integer quantity,
        BigDecimal totalPrice,
        BigDecimal totalTax,
        Integer saleTypeCode,
        Integer paymentTypeCode,
        Instant deliveryDate,
        Instant saleDate,
        Instant paymentDate,
        UUID idCustomer,
        UUID idProduct,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String order,
        int page,
        int size) {

    public static final int DEFAULT_SALE_TYPE = 1; // Unitary, quando string inválida
    public static final int DEFAULT_PAYMENT_TYPE = 1; // Pix, quando string inválida

    /** Zero é o "default" do .NET e nunca filtra. */
    public Integer quantityOrDefault() {
        return quantity == null || quantity == 0 ? null : quantity;
    }

    public BigDecimal totalPriceOrDefault() {
        return totalPrice == null || totalPrice.signum() == 0 ? null : totalPrice;
    }

    public BigDecimal totalTaxOrDefault() {
        return totalTax == null || totalTax.signum() == 0 ? null : totalTax;
    }
}
