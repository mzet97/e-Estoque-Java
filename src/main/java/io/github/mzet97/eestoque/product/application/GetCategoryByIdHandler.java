package io.github.mzet97.eestoque.product.application;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import io.github.mzet97.eestoque.product.domain.CategoryRepository;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BusinessNotificationPublisher;
import io.github.mzet97.eestoque.shared.application.QueryHandler;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;

/** FR-CAT-004 — 404 "Not found" + notification "Not found Category". */
@Component
public class GetCategoryByIdHandler implements QueryHandler<GetCategoryByIdQuery, BaseResult<CategoryViewModel>> {

    /** Cache de referência (ADR-009); literal para não acoplar application→infrastructure. */
    static final String CACHE = "reference:category";

    private final CategoryRepository repository;
    private final BusinessNotificationPublisher notifications;

    public GetCategoryByIdHandler(CategoryRepository repository, BusinessNotificationPublisher notifications) {
        this.repository = repository;
        this.notifications = notifications;
    }

    @Override
    @Cacheable(cacheNames = CACHE, key = "#query.id")
    public BaseResult<CategoryViewModel> handle(GetCategoryByIdQuery query) {
        var category = repository.findById(query.id())
                .orElseThrow(() -> {
                    notifications.publishError("Not found Category", "Not found Category");
                    return new NotFoundException("Not found");
                });

        return BaseResult.of(CategoryViewModel.from(category));
    }
}
