package io.github.mzet97.eestoque.product.application;

import org.springframework.stereotype.Component;

import io.github.mzet97.eestoque.product.domain.CategoryRepository;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.QueryHandler;

/** FR-CAT-005. */
@Component
public class SearchCategoriesHandler implements QueryHandler<SearchCategoriesQuery, BaseResultList<CategoryViewModel>> {

    private final CategoryRepository repository;

    public SearchCategoriesHandler(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public BaseResultList<CategoryViewModel> handle(SearchCategoriesQuery query) {
        var result = repository.search(query.toCriteria());

        var viewModels = result.data().stream().map(CategoryViewModel::from).toList();
        return BaseResultList.of(viewModels, result.pagedResult());
    }
}
