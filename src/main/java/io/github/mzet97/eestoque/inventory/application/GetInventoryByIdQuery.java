package io.github.mzet97.eestoque.inventory.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.Query;

/** FR-INV-004. */
public record GetInventoryByIdQuery(UUID id) implements Query<BaseResult<InventoryViewModel>> {
}
