package io.github.mzet97.eestoque.product.application;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.product.domain.CategoryRepository;
import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.CommandHandler;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;

/** FR-CAT-003 — hard delete; 404 "Delete Error" quando ausente (mensagens .NET). */
@Component
public class DeleteCategoryHandler implements CommandHandler<DeleteCategoryCommand, Void> {

    private final CategoryRepository repository;
    private final BusinessNotificationPublisher notifications;

    public DeleteCategoryHandler(CategoryRepository repository, BusinessNotificationPublisher notifications) {
        this.repository = repository;
        this.notifications = notifications;
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(cacheNames = GetCategoryByIdHandler.CACHE, allEntries = true)
    public Void handle(DeleteCategoryCommand command) {
        var category = repository.findById(command.id())
                .orElseThrow(() -> {
                    notifications.publishError("Delete Category has error", "Delete Category has error");
                    return new NotFoundException("Delete Error");
                });

        repository.deleteById(category.id());
        return null;
    }
}
