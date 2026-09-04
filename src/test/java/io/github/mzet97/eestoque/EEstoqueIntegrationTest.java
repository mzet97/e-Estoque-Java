package io.github.mzet97.eestoque;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.support.TransactionTemplate;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.mzet97.eestoque.outbox.OutboxEvent;
import io.github.mzet97.eestoque.outbox.OutboxSpringDataRepository;
import io.github.mzet97.eestoque.product.application.CreateCategoryCommand;
import io.github.mzet97.eestoque.product.application.CreateCategoryHandler;
import io.github.mzet97.eestoque.shared.infrastructure.messaging.EventWireFormat;

/**
 * NFR-DATA-001/NFR-REL-001: contexto completo contra PostgreSQL real —
 * Flyway V1+V2, validação de schema JPA, criação de categoria e gravação
 * na outbox na mesma transação.
 *
 * Requer Docker (NFR-TEST-002): habilite com -Dintegration=true.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@EnabledIfSystemProperty(named = "integration", matches = "true")
class EEstoqueIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    CreateCategoryHandler createCategory;

    @Autowired
    OutboxSpringDataRepository outbox;

    @Autowired
    TransactionTemplate transactions;

    @Test
    void flywaySchemaMatchesJpaMappingsAndContextStarts() {
        // Se o contexto subiu, Flyway aplicou V1+V2 e o Hibernate validou o schema.
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void createCategoryWritesOutboxEventInSameTransaction() {
        var id = transactions.execute(tx -> createCategory.handle(
                new CreateCategoryCommand("Bebidas", "Bebidas em geral", "Bebidas")));

        assertThat(id).isNotNull();
        var events = outbox.findAll();
        assertThat(events).anySatisfy(event -> {
            assertThat(event.getEventType()).isEqualTo("CategoryCreated");
            assertThat(event.getExchange()).isEqualTo("category-service");
            assertThat(event.getRoutingKey()).isEqualTo("category-created");
            assertThat(event.getStatus()).isEqualTo(OutboxEvent.PENDING);
            assertThat(event.getPayload()).contains("\"name\":\"Bebidas\"");
        });

        UUID unused = UUID.randomUUID();
        Instant ignored = Instant.now();
        assertThat(EventWireFormat.routingKey(new io.github.mzet97.eestoque.product.domain.CategoryUpdated(
                unused, "n", "d", "s"))).isEqualTo("category-updated");
        assertThat(ignored).isNotNull();
    }
}
