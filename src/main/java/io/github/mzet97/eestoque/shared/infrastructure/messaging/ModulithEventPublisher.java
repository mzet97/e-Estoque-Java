package io.github.mzet97.eestoque.shared.infrastructure.messaging;

import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;
import io.github.mzet97.eestoque.shared.domain.NotificationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Implementação dos ports de publicação via Spring Modulith: o evento de
 * domínio entra no Event Publication Registry (persistido na mesma
 * transação do agregado) e é externalizado para o RabbitMQ somente APÓS o
 * commit (ADR-012). O exchange/routing key são declarados na anotação
 * {@link Externalized @Externalized} de cada evento.
 */
@Component
public class ModulithEventPublisher implements DomainEventPublisher, BusinessNotificationPublisher {

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
}
