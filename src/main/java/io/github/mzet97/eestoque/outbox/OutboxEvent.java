package io.github.mzet97.eestoque.outbox;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Evento pendente de publicação, gravado NA MESMA transação do agregado
 * (ADR-012). status: PENDING | PUBLISHED | FAILED.
 */
@Entity
@Table(name = "outbox_events", schema = "public")
public class OutboxEvent {

    public static final String PENDING = "PENDING";
    public static final String PUBLISHED = "PUBLISHED";
    public static final String FAILED = "FAILED";

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "exchange", nullable = false, length = 100)
    private String exchange;

    @Column(name = "routing_key", nullable = false, length = 200)
    private String routingKey;

    @Column(name = "payload", nullable = false, columnDefinition = "text")
    private String payload;

    @Column(name = "status", nullable = false, length = 20)
    private String status = PENDING;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Version
    @Column(name = "version")
    private Long version;

    protected OutboxEvent() {
    }

    public OutboxEvent(UUID id, UUID eventId, String eventType, String exchange, String routingKey, String payload,
                       Instant occurredAt) {
        this.id = id;
        this.eventId = eventId;
        this.eventType = eventType;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getPayload() {
        return payload;
    }

    public String getStatus() {
        return status;
    }

    public void markPublished(Instant at) {
        this.status = PUBLISHED;
        this.processedAt = at;
    }

    public void markFailed(Instant at) {
        this.status = FAILED;
        this.processedAt = at;
    }

    public int getAttempts() {
        return attempts;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
