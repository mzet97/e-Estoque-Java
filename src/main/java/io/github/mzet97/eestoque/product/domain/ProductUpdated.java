package io.github.mzet97.eestoque.product.domain;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.modulith.events.Externalized;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/** Payload idêntico ao ProductUpdated do .NET (com image). */
@Externalized("product-service::product-updated")
public record ProductUpdated(UUID id, String name, String description, String shortDescription, BigDecimal price,
                             BigDecimal weight, BigDecimal height, BigDecimal length, String image, UUID idCategory,
                             UUID idCompany) implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return id;
    }
}
