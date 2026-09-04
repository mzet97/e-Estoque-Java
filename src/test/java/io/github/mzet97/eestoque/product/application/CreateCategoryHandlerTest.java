package io.github.mzet97.eestoque.product.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.github.mzet97.eestoque.product.domain.Category;
import io.github.mzet97.eestoque.product.domain.CategoryCreated;
import io.github.mzet97.eestoque.product.domain.CategoryRepository;
import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

class CreateCategoryHandlerTest {

    private static final Instant NOW = Instant.parse("2026-09-04T12:00:00Z");

    private CategoryRepository repository;
    private DomainEventPublisher eventPublisher;
    private BusinessNotificationPublisher notifications;
    private CreateCategoryHandler handler;

    @BeforeEach
    void setUp() {
        repository = mock(CategoryRepository.class);
        eventPublisher = mock(DomainEventPublisher.class);
        notifications = mock(BusinessNotificationPublisher.class);
        handler = new CreateCategoryHandler(repository, eventPublisher, notifications, Clock.fixed(NOW,
                java.time.ZoneOffset.UTC));
    }

    @Test
    void persistsAndPublishesCreatedEventToCategoryService() {
        var command = new CreateCategoryCommand("Bebidas", "Bebidas em geral", "Bebidas");

        var id = handler.handle(command);

        assertThat(id).isNotNull();
        verify(repository).save(any(Category.class));
        var eventCaptor = ArgumentCaptor.forClass(io.github.mzet97.eestoque.shared.domain.DomainEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture(), eq("category-service"));
        assertThat(eventCaptor.getValue()).isInstanceOf(CategoryCreated.class);
    }

    @Test
    void invalidCommandThrows400WithJoinedMessagesAndNotifiesBroker() {
        var command = new CreateCategoryCommand("a", "a", "a");

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ValidationException.class)
                .hasMessage("The Name need to have between 3 and 80 characters,"
                        + "The ShortDescription need to have between 3 and 500 characters,"
                        + "The Description need to have between 3 and 5000 characters");

        verify(notifications).publishError("Validate Category has error",
                "The Name need to have between 3 and 80 characters,"
                        + "The ShortDescription need to have between 3 and 500 characters,"
                        + "The Description need to have between 3 and 5000 characters");
    }

    @Test
    void updateMissingCategoryThrows404FindErrorAndNotifiesBroker() {
        var updateHandler = new UpdateCategoryHandler(repository, eventPublisher, notifications, Clock.fixed(NOW,
                java.time.ZoneOffset.UTC));
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateHandler.handle(new UpdateCategoryCommand(UUID.randomUUID(), "Bebidas",
                "Bebidas", "Bebidas")))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Find Error");

        verify(notifications).publishError("Update Category has error", "Update Category has error");
    }
}
