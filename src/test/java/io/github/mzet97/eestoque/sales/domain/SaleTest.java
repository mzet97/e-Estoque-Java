package io.github.mzet97.eestoque.sales.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class SaleTest {

    private static final Instant NOW = Instant.parse("2026-09-04T12:00:00Z");

    @Test
    void createValidSaleRaisesSaleCreatedEvent() {
        var customerId = UUID.randomUUID();
        var productId = UUID.randomUUID();

        var sale = Sale.create(2, new BigDecimal("100.50"), new BigDecimal("10.05"), SaleType.UNITARY,
                PaymentType.PIX, NOW, NOW, NOW, customerId, List.of(productId, productId), NOW);

        assertThat(sale.isValid()).isTrue();
        assertThat(sale.saleProducts()).hasSize(2);
        assertThat(sale.saleProducts().getFirst().quantity()).isEqualTo(2);

        var event = (SaleCreated) sale.domainEvents().getFirst();
        assertThat(event.quantity()).isEqualTo(2);
        assertThat(event.totalPrice()).isEqualByComparingTo("100.50");
        assertThat(event.saleType()).isEqualTo(1);
        assertThat(event.paymentType()).isEqualTo(1);
    }

    @Test
    void zeroTotalsProduceFluentValidationMessages() {
        var sale = Sale.create(0, BigDecimal.ZERO, new BigDecimal("-1"), null, null,
                null, NOW, null, UUID.randomUUID(), List.of(), NOW);

        assertThat(sale.errors()).containsExactly(
                "The Quantity needs to be provided",
                "The Quantity needs to be greater than 0",
                "The SaleType needs to be provided",
                "The PaymentType needs to be provided",
                "The TotalPrice needs to be provided",
                "The TotalPrice needs to be greater 0",
                "The TotalTax needs to be greater 0",
                "The DeliveryDate needs to be provided",
                "The PaymentDate needs to be provided");
    }

    @Test
    void negativeTotalProducesGreaterThanMessage() {
        var sale = Sale.create(2, new BigDecimal("-5"), new BigDecimal("1"), SaleType.UNITARY, PaymentType.PIX,
                NOW, NOW, NOW, UUID.randomUUID(), List.of(), NOW);

        assertThat(sale.errors()).containsExactly("The TotalPrice needs to be greater 0");
    }

    @Test
    void softDeleteSetsDeletedFlagsWithoutRemovingData() {
        var sale = Sale.create(2, new BigDecimal("10"), new BigDecimal("1"), SaleType.UNITARY, PaymentType.PIX,
                NOW, NOW, NOW, UUID.randomUUID(), List.of(), NOW);

        sale.disable(NOW.plusSeconds(5));

        assertThat(sale.isDeleted()).isTrue();
        assertThat(sale.deletedAt()).isEqualTo(NOW.plusSeconds(5));
    }

    @Test
    void enumHelpersMirrorDotNetDefaults() {
        assertThat(SaleType.fromStringOrDefault("Recurrent", SaleType.UNITARY)).isEqualTo(SaleType.RECURRENT);
        assertThat(SaleType.fromStringOrDefault("bogus", SaleType.UNITARY)).isEqualTo(SaleType.UNITARY);
        assertThat(PaymentType.fromStringOrDefault("CreditCard", PaymentType.PIX)).isEqualTo(PaymentType.CREDIT_CARD);
        assertThat(PaymentType.fromStringOrDefault("", PaymentType.PIX)).isEqualTo(PaymentType.PIX);
        assertThat(PaymentType.PIX.toInt()).isEqualTo(1);
    }
}
