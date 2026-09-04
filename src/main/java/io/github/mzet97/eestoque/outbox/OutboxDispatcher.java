package io.github.mzet97.eestoque.outbox;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Despacha eventos da outbox para o RabbitMQ com retry/backoff simples.
 * exchanges topic duráveis declaradas no despacho (como o .NET declara no
 * publish). Após MAX_ATTEMPTS o evento é marcado FAILED (dead-letter da
 * outbox) e logado.
 */
@Component
public class OutboxDispatcher {

    private static final Logger log = LoggerFactory.getLogger(OutboxDispatcher.class);
    static final int MAX_ATTEMPTS = 5;

    private final OutboxSpringDataRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxDispatcher(OutboxSpringDataRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public void dispatchPending() {
        List<OutboxEvent> pending = repository.findTop50ByStatusOrderByOccurredAtAsc(OutboxEvent.PENDING);
        for (OutboxEvent event : pending) {
            publish(event);
        }
        List<OutboxEvent> retryable = repository.findTop50ByStatusOrderByOccurredAtAsc(OutboxEvent.FAILED);
        for (OutboxEvent event : retryable) {
            if (event.getAttempts() < MAX_ATTEMPTS) {
                publish(event);
            }
        }
    }

    private void publish(OutboxEvent event) {
        declareExchange(event.getExchange());
        event.incrementAttempts();
        try {
            rabbitTemplate.convertAndSend(event.getExchange(), event.getRoutingKey(), event.getPayload());
            event.markPublished(Instant.now());
        } catch (Exception ex) {
            if (event.getAttempts() >= MAX_ATTEMPTS) {
                event.markFailed(Instant.now());
                log.error("Outbox event {} permanently failed after {} attempts: {}", event.getEventId(),
                        event.getAttempts(), ex.getMessage());
            } else {
                // Permanece PENDING; o agendador é o backoff natural do retry.
                log.warn("Outbox event {} attempt {} failed: {}", event.getEventId(), event.getAttempts(),
                        ex.getMessage());
            }
        }
        repository.save(event);
    }

    private void declareExchange(String exchangeName) {
        rabbitTemplate.execute(channel -> {
            channel.exchangeDeclare(exchangeName, "topic", true);
            return null;
        });
    }

    /**
     * Retry periódico (backoff do agendador); executa em thread de
     * plataforma dedicada (ADR-008).
     */
    @Scheduled(fixedDelayString = "PT5S")
    @Transactional
    public void scheduledDispatch() {
        dispatchPending();
    }

    /** Exchanges conhecidas declaradas no startup (parity com .NET). */
    static TopicExchange[] knownExchanges() {
        return new TopicExchange[] {
                new TopicExchange("product-service", true, false),
                new TopicExchange("category-service", true, false),
                new TopicExchange("company-service", true, false),
                new TopicExchange("customer-service", true, false),
                new TopicExchange("inventory-service", true, false),
                new TopicExchange("sale-service", true, false),
                new TopicExchange("tax-service", true, false),
                new TopicExchange("notification-service", true, false)};
    }
}
