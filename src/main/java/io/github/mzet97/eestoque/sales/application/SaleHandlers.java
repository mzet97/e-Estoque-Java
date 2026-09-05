package io.github.mzet97.eestoque.sales.application;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.sales.domain.PaymentType;
import io.github.mzet97.eestoque.sales.domain.Sale;
import io.github.mzet97.eestoque.sales.domain.SaleReferenceReader;
import io.github.mzet97.eestoque.sales.domain.SaleRepository;
import io.github.mzet97.eestoque.sales.domain.SaleType;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.CommandHandler;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

/** Handlers de Sale (mensagens e ordem .NET exatas; exchange sale-service). */
public final class SaleHandlers {

    public static final String EXCHANGE = "sale-service";

    private SaleHandlers() {
    }

    @Component
    public static class CreateSaleHandler implements CommandHandler<CreateSaleCommand, UUID> {

        private final SaleRepository saleRepository;
        private final SaleReferenceReader referenceReader;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public CreateSaleHandler(SaleRepository saleRepository, SaleReferenceReader referenceReader,
                                 BusinessNotificationPublisher notifications, DomainEventPublisher eventPublisher,
                                 Clock clock) {
            this.saleRepository = saleRepository;
            this.referenceReader = referenceReader;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(CreateSaleCommand command) {
            if (!referenceReader.customerExists(command.idCustomer())) {
                notifications.publishError("Customer not found", "Customer not found");
                throw new NotFoundException("Customer not found");
            }
            for (UUID idProduct : command.idsProducts() == null ? java.util.List.<UUID>of() : command.idsProducts()) {
                if (!referenceReader.productExists(idProduct)) {
                    notifications.publishError("Product not found", "Product not found");
                    throw new NotFoundException("Product not found");
                }
            }

            var sale = Sale.create(command.quantity(), command.totalPrice(), command.totalTax(),
                    toSaleType(command.saleType()), toPaymentType(command.paymentType()), command.deliveryDate(),
                    command.saleDate(), command.paymentDate(), command.idCustomer(), command.idsProducts(),
                    clock.instant());

            if (!sale.isValid()) {
                var errors = sale.joinedErrors();
                notifications.publishError("Validate Sales has error", errors);
                throw new ValidationException(errors);
            }

            saleRepository.save(sale);

            for (DomainEvent event : sale.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return sale.id();
        }
    }

    @Component
    public static class UpdateSaleHandler implements CommandHandler<UpdateSaleCommand, UUID> {

        private final SaleRepository saleRepository;
        private final SaleReferenceReader referenceReader;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public UpdateSaleHandler(SaleRepository saleRepository, SaleReferenceReader referenceReader,
                                 BusinessNotificationPublisher notifications, DomainEventPublisher eventPublisher,
                                 Clock clock) {
            this.saleRepository = saleRepository;
            this.referenceReader = referenceReader;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(UpdateSaleCommand command) {
            var sale = saleRepository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Sale not found", "Sale not found");
                        return new NotFoundException("Sale not found");
                    });

            if (!referenceReader.customerExists(command.idCustomer())) {
                notifications.publishError("Customer not found", "Customer not found");
                throw new NotFoundException("Customer not found");
            }
            for (UUID idProduct : command.idsProducts() == null ? java.util.List.<UUID>of() : command.idsProducts()) {
                if (!referenceReader.productExists(idProduct)) {
                    notifications.publishError("Product not found", "Product not found");
                    throw new NotFoundException("Product not found");
                }
            }

            sale.update(command.quantity(), command.totalPrice(), command.totalTax(),
                    toSaleType(command.saleType()), toPaymentType(command.paymentType()), command.deliveryDate(),
                    command.saleDate(), command.paymentDate(), command.idCustomer(), command.idsProducts(),
                    clock.instant());

            if (!sale.isValid()) {
                var errors = sale.joinedErrors();
                notifications.publishError("Validate Sales has error", errors);
                throw new ValidationException(errors);
            }

            saleRepository.save(sale);

            for (DomainEvent event : sale.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return sale.id();
        }
    }

    @Component
    public static class DeleteSaleHandler implements CommandHandler<DeleteSaleCommand, Void> {

        private final SaleRepository repository;
        private final BusinessNotificationPublisher notifications;
        private final Clock clock;

        public DeleteSaleHandler(SaleRepository repository, BusinessNotificationPublisher notifications,
                                 Clock clock) {
            this.repository = repository;
            this.notifications = notifications;
            this.clock = clock;
        }

        @Override
        @Transactional
        public Void handle(DeleteSaleCommand command) {
            var sale = repository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Delete Sale has error", "Delete Sale has error");
                        return new NotFoundException("Sale not found");
                    });

            repository.disable(sale.id(), clock.instant());
            return null;
        }
    }

    @Component
    public static class GetSaleByIdHandler implements QueryHandler<GetSaleByIdQuery, BaseResult<SaleViewModel>> {

        private final SaleRepository repository;
        private final BusinessNotificationPublisher notifications;

        public GetSaleByIdHandler(SaleRepository repository, BusinessNotificationPublisher notifications) {
            this.repository = repository;
            this.notifications = notifications;
        }

        @Override
        public BaseResult<SaleViewModel> handle(GetSaleByIdQuery query) {
            var viewData = repository.findDetailedById(query.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Sale not found", "Sale not found");
                        return new NotFoundException("Not found");
                    });
            return BaseResult.of(SaleViews.toViewModel(viewData));
        }
    }

    @Component
    public static class SearchSalesHandler implements QueryHandler<SearchSalesQuery, BaseResultList<SaleViewModel>> {

        private final SaleRepository repository;

        public SearchSalesHandler(SaleRepository repository) {
            this.repository = repository;
        }

        @Override
        public BaseResultList<SaleViewModel> handle(SearchSalesQuery query) {
            var result = repository.search(query.toCriteria());
            var viewModels = result.data().stream().map(SaleViews::toViewModel).toList();
            return BaseResultList.of(viewModels, result.pagedResult());
        }
    }

    /**
     * Converte o int do JSON em enum. Valor inválido/null vira null → a
     * validação de domínio produz "The SaleType needs to be provided" (mesmo
     * efeito do binding default 0 do .NET).
     */
    static SaleType toSaleType(Integer code) {
        if (code == null) {
            return null;
        }
        for (SaleType type : SaleType.values()) {
            if (type.toInt() == code) {
                return type;
            }
        }
        return null;
    }

    static PaymentType toPaymentType(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentType type : PaymentType.values()) {
            if (type.toInt() == code) {
                return type;
            }
        }
        return null;
    }
}
