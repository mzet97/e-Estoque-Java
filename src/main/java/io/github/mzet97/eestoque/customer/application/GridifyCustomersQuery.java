package io.github.mzet97.eestoque.customer.application;

import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-CUSTOMER-006. */
public record GridifyCustomersQuery(String filter, String orderBy, Integer page, Integer pageSize, boolean odataDialect)
        implements Query<BaseResultList<CustomerViewModel>> {
}
