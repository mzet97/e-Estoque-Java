package io.github.mzet97.eestoque.shared.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento de domínio: fato ocorrido no agregado. O payload publicado para
 * integração é derivado da implementação concreta (ver 08-EVENT-MODEL.md).
 */
public abstract class DomainEvent {

    private final UUID aggregateId;
    private final Instant occurredAt;

    protected DomainEvent(UUID aggregateId, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.occurredAt = occurredAt;
    }

    public UUID aggregateId() {
        return aggregateId;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
