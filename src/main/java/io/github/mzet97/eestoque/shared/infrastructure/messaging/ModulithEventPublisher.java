package io.github.mzet97.eestoque.shared.infrastructure.messaging;

import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.Externalized;
import org.springframework.stereotype.Component;

/**
 * Implementação dos ports de publicação via Spring Modulith: o evento de
 * domínio entra no Event Publication Registry (persistido na mesma
 * transação do agregado) e é externalizado para o RabbitMQ somente APÓS o
 * commit (ADR-012). O exchange/routing key são declarados na anotação
 * {@link Externalized} de cada evento.
 */
@Component
public class ModulithEventPublisher implements DomainEventPublisher, BusinessNotificationPublisher {

    private static final String NOTIFICATION_EXCHANGE = "notification-service";
    private static final String NOTIFICATION_ROUTING_KEY = "notification-error";

    private final ApplicationEventPublisher events;

    public ModulithEventPublisher(ApplicationEventPublisher events) {
        this.events = events;
    }

    @Override
    public void publish(DomainEvent event, String exchange) {
        // O exchange do parâmetro já está declarado na anotação @Externalized
        // do tipo do evento; o publish dispara registry + externalização.
        events.publishEvent(event);
    }

    @Override
    public void publishError(String message, String details) {
        events.publishEvent(new NotificationEvent(message, details));
    }

    @Externalized(NOTIFICATION_EXCHANGE + "::notification-error")
    public record NotificationEvent(String message, String details) {
    }
}
