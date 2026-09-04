package io.github.mzet97.eestoque.sales.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/**
 * FR-SALE-001. totalPrice/totalTax vêm do cliente (parity .NET); enums
 * chegam como int no JSON.
 */
public record CreateSaleCommand(
        Integer quantity,
        BigDecimal totalPrice,
        BigDecimal totalTax,
        Integer saleType,
        Integer paymentType,
        Instant deliveryDate,
        Instant saleDate,
        Instant paymentDate,
        UUID idCustomer,
        List<UUID> idsProducts) implements Command<UUID> {
}
