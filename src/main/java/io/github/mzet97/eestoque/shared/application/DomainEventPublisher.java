package io.github.mzet97.eestoque.shared.application;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/**
 * Porta de publicação de eventos de integração. Chamada pelos handlers
 * APÓS a persistência; a implementação garante que nada é publicado
 * antes do commit (Transactional Outbox — ADR-012).
 */
public interface DomainEventPublisher {

    /**
     * @param exchange exchange RabbitMQ de destino (ex.: "product-service")
     */
    void publish(DomainEvent event, String exchange);
}
