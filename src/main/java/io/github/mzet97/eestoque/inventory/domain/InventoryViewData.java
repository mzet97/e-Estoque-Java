package io.github.mzet97.eestoque.inventory.domain;

/**
 * Inventário com o Product carregado (include do .NET) para mapear o
 * InventoryViewModel.
 */
public record InventoryViewData(Inventory inventory, ProductSnapshot product) {
}
