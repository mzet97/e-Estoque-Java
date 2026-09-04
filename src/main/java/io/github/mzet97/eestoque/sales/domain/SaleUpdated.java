package io.github.mzet97.eestoque.sales.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/** Payload idêntico ao SaleUpdated do .NET (ver A-20 sobre products). */
public record SaleUpdated(UUID id, Integer quantity, BigDecimal totalPrice, BigDecimal totalTax, Integer saleType,
                          Integer paymentType, Instant deliveryDate, Instant saleDate, Instant paymentDate,
                          UUID idCustomer, List<UUID> products) implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
