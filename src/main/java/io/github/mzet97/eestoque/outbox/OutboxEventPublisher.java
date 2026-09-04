package io.github.mzet97.eestoque.outbox;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;
import io.github.mzet97.eestoque.shared.infrastructure.messaging.EventWireFormat;

/**
 * Publicação via Transactional Outbox (ADR-012): grava o evento na mesma
 * transação do agregado e agenda o despacho AFTER_COMMIT. Nada é publicado
 * antes do commit definitivo.
 */
@Component
public class OutboxEventPublisher implements DomainEventPublisher, BusinessNotificationPublisher {

    public static final String NOTIFICATION_EXCHANGE = "notification-service";

    private final OutboxSpringDataRepository repository;

    private final OutboxDispatcher dispatcher;

    public OutboxEventPublisher(OutboxSpringDataRepository repository, OutboxDispatcher dispatcher) {
        this.repository = repository;
        this.dispatcher = dispatcher;
    }

    @Override
    @Transactional
    public void publish(DomainEvent event, String exchange) {
        var payload = EventWireFormat.serialize(event);
        var outbox = new OutboxEvent(UUID.randomUUID(), UUID.randomUUID(), event.getClass().getSimpleName(),
                exchange, EventWireFormat.routingKey(event), payload, Instant.now());
        repository.save(outbox);
        scheduleDispatch();
    }

    @Override
    @Transactional
    public void publishError(String message, String details) {
        var notification = EventWireFormat.notification(message, details);
        var payload = EventWireFormat.serialize(notification);
        var outbox = new OutboxEvent(UUID.randomUUID(), UUID.randomUUID(), "NotificationError",
                NOTIFICATION_EXCHANGE, "notification-error", payload, Instant.now());
        repository.save(outbox);
        scheduleDispatch();
    }

    private void scheduleDispatch() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                dispatcher.dispatchPending();
            }
        });
    }
}
