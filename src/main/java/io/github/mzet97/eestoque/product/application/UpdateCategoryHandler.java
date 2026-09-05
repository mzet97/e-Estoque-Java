package io.github.mzet97.eestoque.product.application;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.product.domain.CategoryRepository;
import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.CommandHandler;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

/** FR-CAT-002: 404 "Delete Error"? não — 404 com "Find Error" não se aplica aqui; .NET retorna "Delete Error" apenas no delete. */
@Component
public class UpdateCategoryHandler implements CommandHandler<UpdateCategoryCommand, UUID> {

    private final CategoryRepository repository;
    private final DomainEventPublisher eventPublisher;
    private final BusinessNotificationPublisher notifications;
    private final Clock clock;

    public UpdateCategoryHandler(CategoryRepository repository, DomainEventPublisher eventPublisher,
                                 BusinessNotificationPublisher notifications, Clock clock) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.notifications = notifications;
        this.clock = clock;
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(cacheNames = GetCategoryByIdHandler.CACHE, allEntries = true)
    public UUID handle(UpdateCategoryCommand command) {
        var category = repository.findById(command.id())
                .orElseThrow(() -> {
                    notifications.publishError("Update Category has error", "Update Category has error");
                    return new NotFoundException("Find Error");
                });

        category.update(command.name(), command.description(), command.shortDescription(), clock.instant());

        if (!category.isValid()) {
            var errors = category.joinedErrors();
            notifications.publishError("Validate Category has error", errors);
            throw new ValidationException(errors);
        }

        repository.save(category);

        for (DomainEvent event : category.domainEvents()) {
            eventPublisher.publish(event, "category-service");
        }

        return category.id();
    }
}
