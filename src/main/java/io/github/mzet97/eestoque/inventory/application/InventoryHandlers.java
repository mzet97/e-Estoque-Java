package io.github.mzet97.eestoque.inventory.application;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.inventory.domain.Inventory;
import io.github.mzet97.eestoque.inventory.domain.InventoryRepository;
import io.github.mzet97.eestoque.inventory.domain.ProductReader;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.CommandHandler;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

/** Handlers de Inventory (mensagens .NET exatas; exchange inventory-service). */
public final class InventoryHandlers {

    public static final String EXCHANGE = "inventory-service";

    private InventoryHandlers() {
    }

    @Component
    public static class CreateInventoryHandler implements CommandHandler<CreateInventoryCommand, UUID> {

        private final InventoryRepository repository;
        private final ProductReader productReader;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public CreateInventoryHandler(InventoryRepository repository, ProductReader productReader,
                                      BusinessNotificationPublisher notifications,
                                      DomainEventPublisher eventPublisher, Clock clock) {
            this.repository = repository;
            this.productReader = productReader;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(CreateInventoryCommand command) {
            var inventory = Inventory.create(command.quantity(), command.dateOrder(), command.idProduct(),
                    clock.instant());

            if (!inventory.isValid()) {
                var errors = inventory.joinedErrors();
                notifications.publishError("Validate Inventory has error", errors);
                throw new ValidationException(errors);
            }

            if (!productReader.existsById(command.idProduct())) {
                notifications.publishError("Product not found", "Product not found");
                throw new NotFoundException("Product not found");
            }

            repository.save(inventory);

            for (DomainEvent event : inventory.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return inventory.id();
        }
    }

    @Component
    public static class UpdateInventoryHandler implements CommandHandler<UpdateInventoryCommand, UUID> {

        private final InventoryRepository repository;
        private final ProductReader productReader;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public UpdateInventoryHandler(InventoryRepository repository, ProductReader productReader,
                                      BusinessNotificationPublisher notifications,
                                      DomainEventPublisher eventPublisher, Clock clock) {
            this.repository = repository;
            this.productReader = productReader;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(UpdateInventoryCommand command) {
            var inventory = repository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Update Inventory has error", "Update Inventory has error");
                        return new NotFoundException("Find Error");
                    });

            if (!productReader.existsById(command.idProduct())) {
                notifications.publishError("Product not found", "Product not found");
                throw new NotFoundException("Product not found");
            }

            inventory.update(command.quantity(), command.dateOrder(), command.idProduct(), clock.instant());

            if (!inventory.isValid()) {
                var errors = inventory.joinedErrors();
                notifications.publishError("Validate Inventory has error", errors);
                throw new ValidationException(errors);
            }

            repository.save(inventory);

            for (DomainEvent event : inventory.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return inventory.id();
        }
    }

    @Component
    public static class DeleteInventoryHandler implements CommandHandler<DeleteInventoryCommand, Void> {

        private final InventoryRepository repository;
        private final BusinessNotificationPublisher notifications;

        public DeleteInventoryHandler(InventoryRepository repository, BusinessNotificationPublisher notifications) {
            this.repository = repository;
            this.notifications = notifications;
        }

        @Override
        @Transactional
        public Void handle(DeleteInventoryCommand command) {
            var inventory = repository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Delete Inventory has error", "Delete Inventory has error");
                        return new NotFoundException("Delete Error");
                    });

            repository.deleteById(inventory.id());
            return null;
        }
    }

    @Component
    public static class GetInventoryByIdHandler
            implements QueryHandler<GetInventoryByIdQuery, BaseResult<InventoryViewModel>> {

        private final InventoryRepository repository;
        private final BusinessNotificationPublisher notifications;

        public GetInventoryByIdHandler(InventoryRepository repository, BusinessNotificationPublisher notifications) {
            this.repository = repository;
            this.notifications = notifications;
        }

        @Override
        public BaseResult<InventoryViewModel> handle(GetInventoryByIdQuery query) {
            var viewData = repository.findDetailedById(query.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Not found Inventory", "Not found Inventory");
                        return new NotFoundException("Not found");
                    });
            return BaseResult.of(InventoryViews.toViewModel(viewData));
        }
    }

    @Component
    public static class SearchInventoriesHandler
            implements QueryHandler<SearchInventoriesQuery, BaseResultList<InventoryViewModel>> {

        private final InventoryRepository repository;

        public SearchInventoriesHandler(InventoryRepository repository) {
            this.repository = repository;
        }

        @Override
        public BaseResultList<InventoryViewModel> handle(SearchInventoriesQuery query) {
            var result = repository.search(query.toCriteria());
            var viewModels = result.data().stream().map(InventoryViews::toViewModel).toList();
            return BaseResultList.of(viewModels, result.pagedResult());
        }
    }
}
