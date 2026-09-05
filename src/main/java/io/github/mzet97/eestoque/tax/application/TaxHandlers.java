package io.github.mzet97.eestoque.tax.application;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.CommandHandler;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;
import io.github.mzet97.eestoque.shared.domain.ValidationException;
import io.github.mzet97.eestoque.tax.domain.Tax;
import io.github.mzet97.eestoque.tax.domain.TaxRepository;

/**
 * Handlers de Tax. Como no .NET, create/update NÃO verificam a existência
 * da Category (apenas FK no banco — ASSUMPTIONS A-07). O update usa
 * details=errors na notificação, replicando o original.
 */
public final class TaxHandlers {

    public static final String EXCHANGE = "tax-service";

    private TaxHandlers() {
    }

    @Component
    public static class CreateTaxHandler implements CommandHandler<CreateTaxCommand, UUID> {

        private final TaxRepository repository;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public CreateTaxHandler(TaxRepository repository, BusinessNotificationPublisher notifications,
                                DomainEventPublisher eventPublisher, Clock clock) {
            this.repository = repository;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(CreateTaxCommand command) {
            var tax = Tax.create(command.name(), command.description(), command.percentage(), command.idCategory(),
                    clock.instant());

            if (!tax.isValid()) {
                var errors = tax.joinedErrors();
                notifications.publishError("Validate Tax has error", errors);
                throw new ValidationException(errors);
            }

            repository.save(tax);

            for (DomainEvent event : tax.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return tax.id();
        }
    }

    @Component
    public static class UpdateTaxHandler implements CommandHandler<UpdateTaxCommand, UUID> {

        private final TaxRepository repository;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public UpdateTaxHandler(TaxRepository repository, BusinessNotificationPublisher notifications,
                                DomainEventPublisher eventPublisher, Clock clock) {
            this.repository = repository;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(UpdateTaxCommand command) {
            var tax = repository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Update Tax has error", "Update Tax has error");
                        return new NotFoundException("Find Error");
                    });

            tax.update(command.name(), command.description(), command.percentage(), command.idCategory(),
                    clock.instant());

            if (!tax.isValid()) {
                var errors = tax.joinedErrors();
                notifications.publishError("Validate Tax has error", errors);
                throw new ValidationException(errors);
            }

            repository.save(tax);

            for (DomainEvent event : tax.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return tax.id();
        }
    }

    @Component
    public static class DeleteTaxHandler implements CommandHandler<DeleteTaxCommand, Void> {

        private final TaxRepository repository;
        private final BusinessNotificationPublisher notifications;

        public DeleteTaxHandler(TaxRepository repository, BusinessNotificationPublisher notifications) {
            this.repository = repository;
            this.notifications = notifications;
        }

        @Override
        @Transactional
        public Void handle(DeleteTaxCommand command) {
            var tax = repository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Delete Tax has error", "Delete Tax has error");
                        return new NotFoundException("Delete Error");
                    });

            repository.deleteById(tax.id());
            return null;
        }
    }

    @Component
    public static class GetTaxByIdHandler implements QueryHandler<GetTaxByIdQuery, BaseResult<TaxViewModel>> {

        private final TaxRepository repository;
        private final BusinessNotificationPublisher notifications;

        public GetTaxByIdHandler(TaxRepository repository, BusinessNotificationPublisher notifications) {
            this.repository = repository;
            this.notifications = notifications;
        }

        @Override
        public BaseResult<TaxViewModel> handle(GetTaxByIdQuery query) {
            var viewData = repository.findDetailedById(query.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Not found Tax", "Not found Tax");
                        return new NotFoundException("Not found");
                    });
            return BaseResult.of(TaxViews.toViewModel(viewData));
        }
    }

    @Component
    public static class SearchTaxesHandler implements QueryHandler<SearchTaxesQuery, BaseResultList<TaxViewModel>> {

        private final TaxRepository repository;

        public SearchTaxesHandler(TaxRepository repository) {
            this.repository = repository;
        }

        @Override
        public BaseResultList<TaxViewModel> handle(SearchTaxesQuery query) {
            var result = repository.search(query.toCriteria());
            var viewModels = result.data().stream().map(TaxViews::toViewModel).toList();
            return BaseResultList.of(viewModels, result.pagedResult());
        }
    }
}
