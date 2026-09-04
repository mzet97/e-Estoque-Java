package io.github.mzet97.eestoque.shared.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/**
 * Implementação provisória das portas de publicação: registra os eventos
 * em log. Substituída pelo Outbox + Spring AMQP (Slice 9 / ADR-012).
 */
@Component
public class LoggingEventPublisher implements DomainEventPublisher, BusinessNotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisher.class);

    @Override
    public void publish(DomainEvent event, String exchange) {
        log.info("event={} exchange={} routingKey={} aggregateId={}",
                event.getClass().getSimpleName(), exchange, routingKey(event), event.aggregateId());
    }

    @Override
    public void publishError(String message, String details) {
        log.warn("notification exchange=notification-service message={} details={}", message, details);
    }

    /** Nome da classe em dash-case, como ToDashCase do .NET. */
    static String routingKey(DomainEvent event) {
        return toDashCase(event.getClass().getSimpleName());
    }

    static String toDashCase(String name) {
        if (name == null || name.length() < 2) {
            return name;
        }
        var sb = new StringBuilder();
        sb.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('-').append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
