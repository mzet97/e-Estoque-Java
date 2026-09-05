package io.github.mzet97.eestoque.customer.application;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.customer.domain.Customer;
import io.github.mzet97.eestoque.customer.domain.CustomerRepository;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.CommandHandler;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

/** Handlers de Customer (mensagens .NET exatas; exchange customer-service). */
public final class CustomerHandlers {

    public static final String EXCHANGE = "customer-service";

    private CustomerHandlers() {
    }

    @Component
    public static class CreateCustomerHandler implements CommandHandler<CreateCustomerCommand, UUID> {

        private final CustomerRepository repository;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public CreateCustomerHandler(CustomerRepository repository, BusinessNotificationPublisher notifications,
                                     DomainEventPublisher eventPublisher, Clock clock) {
            this.repository = repository;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(CreateCustomerCommand command) {
            var customer = Customer.create(command.name(), command.docId(), command.email(), command.description(),
                    command.phoneNumber(), command.customerAddress(), clock.instant());

            if (!customer.isValid()) {
                var errors = customer.joinedErrors();
                notifications.publishError("Validate Customer has error", errors);
                throw new ValidationException(errors);
            }

            repository.save(customer);

            for (DomainEvent event : customer.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return customer.id();
        }
    }

    @Component
    public static class UpdateCustomerHandler implements CommandHandler<UpdateCustomerCommand, UUID> {

        private final CustomerRepository repository;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public UpdateCustomerHandler(CustomerRepository repository, BusinessNotificationPublisher notifications,
                                     DomainEventPublisher eventPublisher, Clock clock) {
            this.repository = repository;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(UpdateCustomerCommand command) {
            var customer = repository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Update Customer has error", "Update Customer has error");
                        return new NotFoundException("Find Error");
                    });

            customer.update(command.name(), command.docId(), command.email(), command.description(),
                    command.phoneNumber(), command.customerAddress(), clock.instant());

            if (!customer.isValid()) {
                var errors = customer.joinedErrors();
                notifications.publishError("Validate Customer has error", errors);
                throw new ValidationException(errors);
            }

            repository.save(customer);

            for (DomainEvent event : customer.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return customer.id();
        }
    }

    @Component
    public static class DeleteCustomerHandler implements CommandHandler<DeleteCustomerCommand, Void> {

        private final CustomerRepository repository;
        private final BusinessNotificationPublisher notifications;

        public DeleteCustomerHandler(CustomerRepository repository, BusinessNotificationPublisher notifications) {
            this.repository = repository;
            this.notifications = notifications;
        }

        @Override
        @Transactional
        public Void handle(DeleteCustomerCommand command) {
            var customer = repository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Delete Customer has error", "Delete Customer has error");
                        return new NotFoundException("Delete Error");
                    });

            repository.deleteById(customer.id());
            return null;
        }
    }

    @Component
    public static class GetCustomerByIdHandler
            implements QueryHandler<GetCustomerByIdQuery, BaseResult<CustomerViewModel>> {

        private final CustomerRepository repository;
        private final BusinessNotificationPublisher notifications;

        public GetCustomerByIdHandler(CustomerRepository repository, BusinessNotificationPublisher notifications) {
            this.repository = repository;
            this.notifications = notifications;
        }

        @Override
        public BaseResult<CustomerViewModel> handle(GetCustomerByIdQuery query) {
            var customer = repository.findById(query.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Not found Customer", "Not found Customer");
                        return new NotFoundException("Not found");
                    });
            return BaseResult.of(CustomerViewModel.from(customer));
        }
    }

    @Component
    public static class SearchCustomersHandler
            implements QueryHandler<SearchCustomersQuery, BaseResultList<CustomerViewModel>> {

        private final CustomerRepository repository;

        public SearchCustomersHandler(CustomerRepository repository) {
            this.repository = repository;
        }

        @Override
        public BaseResultList<CustomerViewModel> handle(SearchCustomersQuery query) {
            var result = repository.search(query.toCriteria());
            return BaseResultList.of(result.data().stream().map(CustomerViewModel::from).toList(),
                    result.pagedResult());
        }
    }
}
