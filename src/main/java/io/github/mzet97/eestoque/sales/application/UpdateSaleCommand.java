package io.github.mzet97.eestoque.sales.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;
import jakarta.validation.constraints.NotNull;

/** FR-SALE-002 — id da rota prevalece (aqui o .NET já fazia isso). */
public record UpdateSaleCommand(
        UUID id,
        Integer quantity,
        BigDecimal totalPrice,
        BigDecimal totalTax,
        Integer saleType,
        Integer paymentType,
        Instant deliveryDate,
        Instant saleDate,
        Instant paymentDate,
        @NotNull UUID idCustomer,
        List<UUID> idsProducts) implements Command<UUID> {
}
