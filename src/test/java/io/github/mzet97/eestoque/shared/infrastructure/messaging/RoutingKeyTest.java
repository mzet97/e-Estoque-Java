package io.github.mzet97.eestoque.shared.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

class RoutingKeyTest {

    static final class SampleCreated implements DomainEvent {
        private final UUID id;
        private final Instant occurredAt;

        SampleCreated(UUID id, Instant occurredAt) {
            this.id = id;
            this.occurredAt = occurredAt;
        }

        @Override
        public UUID aggregateId() {
            return id;
        }

        public Instant occurredAt() {
            return occurredAt;
        }
    }

    @Test
    void convertsClassNameToDashCaseLikeDotNet() {
        var event = new SampleCreated(UUID.randomUUID(), Instant.now());

        assertThat(LoggingEventPublisher.routingKey(event)).isEqualTo("sample-created");
    }

    @Test
    void dashCaseHandlesConsecutiveCapitals() {
        assertThat(LoggingEventPublisher.toDashCase("ProductCreated")).isEqualTo("product-created");
        assertThat(LoggingEventPublisher.toDashCase("a")).isEqualTo("a");
    }
}
