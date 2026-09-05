package io.github.mzet97.eestoque.product.application;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.product.domain.CategoryRepository;
import io.github.mzet97.eestoque.product.domain.CompanyReader;
import io.github.mzet97.eestoque.product.domain.Product;
import io.github.mzet97.eestoque.product.domain.ProductRepository;
import io.github.mzet97.eestoque.product.domain.ProductViewData;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.CommandHandler;
import io.github.mzet97.eestoque.shared.application.DomainEventPublisher;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.domain.DomainEvent;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

/**
 * Handlers de Product. Ordem .NET preservada: valida entidade → checa
 * Category → checa Company → persiste → publica em product-service.
 */
public final class ProductHandlers {

    public static final String EXCHANGE = "product-service";

    private ProductHandlers() {
    }

    @Component
    public static class CreateProductHandler implements CommandHandler<CreateProductCommand, UUID> {

        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;
        private final CompanyReader companyReader;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public CreateProductHandler(ProductRepository productRepository, CategoryRepository categoryRepository,
                                    CompanyReader companyReader, BusinessNotificationPublisher notifications,
                                    DomainEventPublisher eventPublisher, Clock clock) {
            this.productRepository = productRepository;
            this.categoryRepository = categoryRepository;
            this.companyReader = companyReader;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(CreateProductCommand command) {
            var product = Product.create(command.name(), command.description(), command.shortDescription(),
                    command.price(), command.weight(), command.height(), command.length(), command.image(),
                    command.idCategory(), command.idCompany(), clock.instant());

            if (!product.isValid()) {
                var errors = product.joinedErrors();
                notifications.publishError("Validate Product has error", errors);
                throw new ValidationException(errors);
            }

            if (categoryRepository.findById(command.idCategory()).isEmpty()) {
                notifications.publishError("Category not found", "Category not found");
                throw new NotFoundException("Category not found");
            }
            if (!companyReader.existsById(command.idCompany())) {
                notifications.publishError("Company not found", "Company not found");
                throw new NotFoundException("Company not found");
            }

            productRepository.save(product);

            for (DomainEvent event : product.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return product.id();
        }
    }

    @Component
    public static class UpdateProductHandler implements CommandHandler<UpdateProductCommand, UUID> {

        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;
        private final CompanyReader companyReader;
        private final BusinessNotificationPublisher notifications;
        private final DomainEventPublisher eventPublisher;
        private final Clock clock;

        public UpdateProductHandler(ProductRepository productRepository, CategoryRepository categoryRepository,
                                    CompanyReader companyReader, BusinessNotificationPublisher notifications,
                                    DomainEventPublisher eventPublisher, Clock clock) {
            this.productRepository = productRepository;
            this.categoryRepository = categoryRepository;
            this.companyReader = companyReader;
            this.notifications = notifications;
            this.eventPublisher = eventPublisher;
            this.clock = clock;
        }

        @Override
        @Transactional
        public UUID handle(UpdateProductCommand command) {
            var product = productRepository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Update Product has error", "Update Product has error");
                        return new NotFoundException("Find Error");
                    });

            if (categoryRepository.findById(command.idCategory()).isEmpty()) {
                notifications.publishError("Category not found", "Category not found");
                throw new NotFoundException("Category not found");
            }
            if (!companyReader.existsById(command.idCompany())) {
                notifications.publishError("Company not found", "Company not found");
                throw new NotFoundException("Company not found");
            }

            product.update(command.name(), command.description(), command.shortDescription(), command.price(),
                    command.weight(), command.height(), command.length(), command.image(), command.idCategory(),
                    command.idCompany(), clock.instant());

            if (!product.isValid()) {
                var errors = product.joinedErrors();
                notifications.publishError("Validate Product has error", errors);
                throw new ValidationException(errors);
            }

            productRepository.save(product);

            for (DomainEvent event : product.domainEvents()) {
                eventPublisher.publish(event, EXCHANGE);
            }
            return product.id();
        }
    }

    @Component
    public static class DeleteProductHandler implements CommandHandler<DeleteProductCommand, Void> {

        private final ProductRepository repository;
        private final BusinessNotificationPublisher notifications;

        public DeleteProductHandler(ProductRepository repository, BusinessNotificationPublisher notifications) {
            this.repository = repository;
            this.notifications = notifications;
        }

        @Override
        @Transactional
        public Void handle(DeleteProductCommand command) {
            var product = repository.findById(command.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Delete Product has error", "Delete Product has error");
                        return new NotFoundException("Delete Error");
                    });

            repository.deleteById(product.id());
            return null;
        }
    }

    @Component
    public static class GetProductByIdHandler
            implements QueryHandler<GetProductByIdQuery, BaseResult<ProductViewModel>> {

        private final ProductRepository repository;
        private final BusinessNotificationPublisher notifications;

        public GetProductByIdHandler(ProductRepository repository, BusinessNotificationPublisher notifications) {
            this.repository = repository;
            this.notifications = notifications;
        }

        @Override
        public BaseResult<ProductViewModel> handle(GetProductByIdQuery query) {
            var viewData = repository.findDetailedById(query.id())
                    .orElseThrow(() -> {
                        notifications.publishError("Not found Product", "Not found Product");
                        return new NotFoundException("Not found");
                    });
            return BaseResult.of(ProductViews.toViewModel(viewData));
        }
    }

    @Component
    public static class SearchProductsHandler
            implements QueryHandler<SearchProductsQuery, BaseResultList<ProductViewModel>> {

        private final ProductRepository repository;

        public SearchProductsHandler(ProductRepository repository) {
            this.repository = repository;
        }

        @Override
        public BaseResultList<ProductViewModel> handle(SearchProductsQuery query) {
            var result = repository.search(query.toCriteria());
            var viewModels = result.data().stream().map(ProductViews::toViewModel).toList();
            return BaseResultList.of(viewModels, result.pagedResult());
        }
    }
}
