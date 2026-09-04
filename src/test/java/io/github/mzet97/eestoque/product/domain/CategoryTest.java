package io.github.mzet97.eestoque.product.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

class CategoryTest {

    private static final Instant NOW = Instant.parse("2026-09-04T12:00:00Z");

    @Test
    void createWithValidFieldsIsValidAndRaisesCreatedEvent() {
        var category = Category.create("Bebidas", "Bebidas em geral", "Bebidas", NOW);

        assertThat(category.isValid()).isTrue();
        assertThat(category.errors()).isEmpty();
        assertThat(category.id()).isNotNull();
        assertThat(category.createdAt()).isEqualTo(NOW);

        var events = category.domainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(CategoryCreated.class);
    }

    @Test
    void validationMessagesMirrorDotNetFluentValidation() {
        var category = Category.create("a", "a", "a", NOW);

        assertThat(category.isValid()).isFalse();
        assertThat(category.errors()).containsExactly(
                "The Name need to have between 3 and 80 characters",
                "The ShortDescription need to have between 3 and 500 characters",
                "The Description need to have between 3 and 5000 characters");
    }

    @Test
    void blankFieldsProduceProvidedAndLengthMessagesLikeFluentValidation() {
        var category = Category.create("", "", "  ", NOW);

        assertThat(category.errors()).containsExactly(
                "The Name needs to be provided",
                "The Name need to have between 3 and 80 characters",
                "The ShortDescription needs to be provided",
                "The ShortDescription need to have between 3 and 500 characters",
                "The Description needs to be provided",
                "The Description need to have between 3 and 5000 characters");
    }

    @Test
    void updateTouchesTimestampAndRaisesUpdatedEvent() {
        var category = Category.create("Bebidas", "Bebidas em geral", "Bebidas", NOW);
        category.clearDomainEvents();
        var later = NOW.plusSeconds(60);

        category.update("Alimentos", "Alimentos em geral", "Alimentos", later);

        assertThat(category.updatedAt()).isEqualTo(later);
        assertThat(category.isValid()).isTrue();
        assertThat(category.domainEvents()).hasSize(1);
        assertThat(category.domainEvents().getFirst()).isInstanceOf(CategoryUpdated.class);
    }

    @Test
    void joinedErrorsUsesCommaWithoutSpaceLikeDotNet() {
        var category = Category.create("a", "a", "a", NOW);

        assertThat(category.joinedErrors()).isEqualTo(
                "The Name need to have between 3 and 80 characters,"
                        + "The ShortDescription need to have between 3 and 500 characters,"
                        + "The Description need to have between 3 and 5000 characters");
    }
}
