package io.github.mzet97.eestoque.sales.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.modulith.events.Externalized;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/**
 * Payload idêntico ao SaleCreated do .NET, incluindo o comportamento
 * original de "products" conter elementos null no momento da criação
 * (navegação Product não carregada — ASSUMPTIONS A-20).
 */
@Externalized("sale-service::sale-created")
public record SaleCreated(UUID id, Integer quantity, BigDecimal totalPrice, BigDecimal totalTax, Integer saleType,
                          Integer paymentType, Instant deliveryDate, Instant saleDate, Instant paymentDate,
                          UUID idCustomer, List<UUID> products) implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
