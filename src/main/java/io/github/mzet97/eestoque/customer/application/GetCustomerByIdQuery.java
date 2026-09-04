package io.github.mzet97.eestoque.customer.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-CUSTOMER-004. */
public record GetCustomerByIdQuery(UUID id) implements Query<BaseResult<CustomerViewModel>> {
}
