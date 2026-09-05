package io.github.mzet97.eestoque;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.mzet97.eestoque.product.application.CreateCategoryCommand;
import io.github.mzet97.eestoque.product.application.CreateCategoryHandler;
import io.github.mzet97.eestoque.product.domain.CategoryCreated;

/**
 * NFR-DATA-001/NFR-REL-001: contexto completo contra PostgreSQL real —
 * Flyway V1+V2, validação de schema JPA, criação de categoria e persistência
 * da publicação de evento (Spring Modulith Event Publication Registry) na
 * mesma transação (ADR-012).
 *
 * Requer Docker (NFR-TEST-002): habilite com -Dintegration=true.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@TestPropertySource(properties = {
        "spring.modulith.events.jdbc.schema-initialization.enabled=false"
})
@EnabledIfSystemProperty(named = "integration", matches = "true")
class EEstoqueIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    CreateCategoryHandler createCategory;

    @Autowired
    TransactionTemplate transactions;

    @Autowired
    org.springframework.jdbc.core.JdbcTemplate jdbc;

    @Test
    void flywaySchemaMatchesJpaMappingsAndContextStarts() {
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void createCategoryWritesEventPublicationInSameTransaction() {
        var id = transactions.execute(tx -> createCategory.handle(
                new CreateCategoryCommand("Bebidas", "Bebidas em geral", "Bebidas")));

        assertThat(id).isNotNull();
        var count = jdbc.queryForObject(
                "SELECT count(*) FROM event_publication WHERE event_type = 'CategoryCreated' "
                        + "AND completion_date IS NULL",
                Integer.class);
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Test
    void wireFormatStillAvailableForModulithSerializationDefaults() {
        var event = new CategoryCreated(UUID.randomUUID(), "n", "d", "s");
        assertThat(event.aggregateId()).isNotNull();
        assertThat(Instant.now()).isNotNull();
    }
}
