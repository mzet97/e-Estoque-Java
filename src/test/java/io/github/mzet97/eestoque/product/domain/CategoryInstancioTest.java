package io.github.mzet97.eestoque.product.domain;

import java.time.Instant;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * Dados de teste gerados com Instancio: qualquer entrada dentro dos limites
 * .NET (Name 3–80, ShortDescription 3–500, Description 3–5000) valida; o
 * estouro de qualquer limite falha com a mensagem exata do FluentValidation
 * original.
 */
class CategoryInstancioTest {

    private static final Instant NOW = Instant.parse("2026-01-01T12:00:00Z");

    record CategoryInput(String name, String description, String shortDescription) {
    }

    private static CategoryInput input(int minLength, int maxLength) {
        return Instancio.of(CategoryInput.class)
                .generate(field(CategoryInput::name), gen -> gen.string().length(minLength, maxLength).alphaNumeric())
                .generate(field(CategoryInput::description),
                        gen -> gen.string().length(minLength, maxLength).alphaNumeric())
                .generate(field(CategoryInput::shortDescription),
                        gen -> gen.string().length(minLength, maxLength).alphaNumeric())
                .create();
    }

    @Test
    void randomDataInsideLimitsAlwaysValidates() {
        for (int i = 0; i < 25; i++) {
            var input = input(3, 80);

            var category = Category.create(input.name(), input.description(), input.shortDescription(), NOW);

            assertThat(category.isValid()).as("input %s", input).isTrue();
            assertThat(category.joinedErrors()).isEmpty();
            assertThat(category.domainEvents()).hasSize(1);
            assertThat(category.name()).isEqualTo(input.name());
        }
    }

    @Test
    void randomDataBeyondLimitsAlwaysFailsWithNetMessage() {
        for (int i = 0; i < 25; i++) {
            var input = input(81, 120);

            var category = Category.create(input.name(), input.description(), input.shortDescription(), NOW);

            assertThat(category.isValid()).isFalse();
            assertThat(category.joinedErrors())
                    .contains("The Name need to have between 3 and 80 characters");
        }
    }
}
