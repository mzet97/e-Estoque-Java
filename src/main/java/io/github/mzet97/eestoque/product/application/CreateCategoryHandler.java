package io.github.mzet97.eestoque.product.application;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.product.domain.Category;
import io.github.mzet97.eestoque.product.domain.CategoryRepository;
import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.CommandHandler;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

/**
 * FR-CAT-001. Fluxo fiel ao .NET: valida a entidade (400 com mensagens
 * unidas), notifica o broker, persiste e publica os eventos em
 * category-service (rk category-created).
 */
@Component
public class CreateCategoryHandler implements CommandHandler<CreateCategoryCommand, UUID> {

    public static final String EXCHANGE = "category-service";

    private final CategoryRepository repository;
    private final DomainEventPublisher eventPublisher;
    private final BusinessNotificationPublisher notifications;
    private final Clock clock;

    public CreateCategoryHandler(CategoryRepository repository, DomainEventPublisher eventPublisher,
                                 BusinessNotificationPublisher notifications, Clock clock) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.notifications = notifications;
        this.clock = clock;
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(cacheNames = GetCategoryByIdHandler.CACHE, allEntries = true)
    public UUID handle(CreateCategoryCommand command) {
        var category = Category.create(command.name(), command.description(), command.shortDescription(),
                clock.instant());

        if (!category.isValid()) {
            var errors = category.joinedErrors();
            notifications.publishError("Validate Category has error", errors);
            throw new ValidationException(errors);
        }

        repository.save(category);

        for (DomainEvent event : category.domainEvents()) {
            eventPublisher.publish(event, EXCHANGE);
        }

        return category.id();
    }
}
