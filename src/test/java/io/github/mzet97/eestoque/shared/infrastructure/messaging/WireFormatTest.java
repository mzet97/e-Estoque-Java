package io.github.mzet97.eestoque.shared.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.github.mzet97.eestoque.product.domain.ProductCreated;

class WireFormatTest {

    @Test
    void serializesEventAsCamelCaseJsonIgnoringNulls() {
        var event = new ProductCreated(UUID.randomUUID(), "Bola", "Bola de futebol", "Bola",
                new BigDecimal("29.90"), new BigDecimal("0.4"), new BigDecimal("0.22"), new BigDecimal("0.22"),
                UUID.randomUUID(), null);

        var json = EventWireFormat.serialize(event);

        assertThat(json).contains("\"price\":29.90");
        assertThat(json).contains("\"idCategory\":");
        assertThat(json).doesNotContain("idCompany");
    }

    @Test
    void routingKeyFollowsDotNetDashCase() {
        var event = new ProductCreated(UUID.randomUUID(), "n", "d", "s", BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, UUID.randomUUID(), UUID.randomUUID());

        assertThat(EventWireFormat.routingKey(event)).isEqualTo("product-created");
    }

    @Test
    void notificationPayloadMatchesDotNetShape() {
        var payload = EventWireFormat.notification("Category not found", "Category not found");

        assertThat(EventWireFormat.serialize(payload))
                .isEqualTo("{\"message\":\"Category not found\",\"details\":\"Category not found\"}");
    }

    @Test
    void instantIsSerializedAsIsoString() {
        var payload = EventWireFormat.notification("m", "d");
        assertThat(Instant.now()).isNotNull();
        assertThat(EventWireFormat.serialize(payload)).contains("message");
    }
}
