package io.github.mzet97.eestoque.company.application;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.company.domain.Company;
import io.github.mzet97.eestoque.company.domain.CompanyRepository;
import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.CommandHandler;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.application.SearchResult;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

/**
 * Handlers de Company. Mensagens e ordem de verificação idênticas ao .NET;
 * eventos publicados no exchange company-service.
 */
public final class CompanyHandlers {

    public static final String EXCHANGE = "company-service";

    private CompanyHandlers() {
    }

    @Component
    public static class CreateCompanyHandler implements CommandHandler<CreateCompanyCommand, UUID> {

        private final CompanyRepository repository;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public CreateCompanyHandler(CompanyRepository repository, BusinessNotificationPublisher notifications,
                                    DomainEventPublisher eventPublisher, Clock clock) {
            this.repository = repository;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(CreateCompanyCommand command) {
            var company = Company.create(command.name(), command.docId(), command.email(), command.description(),
                    command.phoneNumber(), command.companyAddress(), clock.instant());

            if (!company.isValid()) {
                var errors = company.joinedErrors();
                notifications.publishError("Validate Company has error", errors);
                throw new ValidationException(errors);
            }

            repository.save(company);

            for (DomainEvent event : company.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return company.id();
        }
    }

    @Component
    public static class UpdateCompanyHandler implements CommandHandler<UpdateCompanyCommand, UUID> {

        private final CompanyRepository repository;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public UpdateCompanyHandler(CompanyRepository repository, BusinessNotificationPublisher notifications,
                                    DomainEventPublisher eventPublisher, Clock clock) {
            this.repository = repository;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(UpdateCompanyCommand command) {
            var company = repository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Update Company has error", "Update Company has error");
                        return new NotFoundException("Find Error");
                    });

            company.update(command.name(), command.docId(), command.email(), command.description(),
                    command.phoneNumber(), command.companyAddress(), clock.instant());

            if (!company.isValid()) {
                var errors = company.joinedErrors();
                notifications.publishError("Validate Company has error", errors);
                throw new ValidationException(errors);
            }

            repository.save(company);

            for (DomainEvent event : company.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return company.id();
        }
    }

    @Component
    public static class DeleteCompanyHandler implements CommandHandler<DeleteCompanyCommand, Void> {

        private final CompanyRepository repository;
        private final BusinessNotificationPublisher notifications;

        public DeleteCompanyHandler(CompanyRepository repository, BusinessNotificationPublisher notifications) {
            this.repository = repository;
            this.notifications = notifications;
        }

        @Override
        @Transactional
        public Void handle(DeleteCompanyCommand command) {
            var company = repository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Delete Company has error", "Delete Company has error");
                        return new NotFoundException("Delete Error");
                    });

            repository.deleteById(company.id());
            return null;
        }
    }

    @Component
    public static class GetCompanyByIdHandler
            implements QueryHandler<GetCompanyByIdQuery, BaseResult<CompanyViewModel>> {

        private final CompanyRepository repository;
        private final BusinessNotificationPublisher notifications;

        public GetCompanyByIdHandler(CompanyRepository repository, BusinessNotificationPublisher notifications) {
            this.repository = repository;
            this.notifications = notifications;
        }

        @Override
        public BaseResult<CompanyViewModel> handle(GetCompanyByIdQuery query) {
            var company = repository.findById(query.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Not found Company", "Not found Company");
                        return new NotFoundException("Not found");
                    });
            return BaseResult.of(CompanyViewModel.from(company));
        }
    }

    @Component
    public static class SearchCompaniesHandler
            implements QueryHandler<SearchCompaniesQuery, BaseResultList<CompanyViewModel>> {

        private final CompanyRepository repository;

        public SearchCompaniesHandler(CompanyRepository repository) {
            this.repository = repository;
        }

        @Override
        public BaseResultList<CompanyViewModel> handle(SearchCompaniesQuery query) {
            SearchResult<io.github.mzet97.eestoque.company.domain.Company> result = repository.search(query.toCriteria());
            return BaseResultList.of(result.data().stream().map(CompanyViewModel::from).toList(),
                    result.pagedResult());
        }
    }
}
