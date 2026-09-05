package io.github.mzet97.eestoque.product.domain;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.modulith.events.Externalized;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/** Payload idêntico ao ProductCreated do .NET (sem image). */
@Externalized("product-service::product-created")
public record ProductCreated(UUID id, String name, String description, String shortDescription, BigDecimal price,
                             BigDecimal weight, BigDecimal height, BigDecimal length, UUID idCategory,
                             UUID idCompany) implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
