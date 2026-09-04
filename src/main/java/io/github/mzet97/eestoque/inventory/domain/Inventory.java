package io.github.mzet97.eestoque.inventory.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.Entity;
import io.github.mzet97.eestoque.shared.domain.validation.Checks;

/**
 * Movimento de estoque. Invariantes do InventoryValidation .NET:
 * Quantity > 0, DateOrder obrigatória, IdProduct obrigatório.
 * Criar/atualizar inventário NÃO altera produto nem venda (parity).
 */
public final class Inventory extends Entity {

    private Integer quantity;
    private Instant dateOrder;
    private UUID idProduct;

    private Inventory(UUID id, Integer quantity, Instant dateOrder, UUID idProduct, Instant createdAt,
                      Instant updatedAt, Instant deletedAt, boolean deleted) {
        super(id, createdAt, updatedAt, deletedAt, deleted);
        this.quantity = quantity;
        this.dateOrder = dateOrder;
        this.idProduct = idProduct;
    }

    public static Inventory create(Integer quantity, Instant dateOrder, UUID idProduct, Instant now) {
        var inventory = new Inventory(UUID.randomUUID(), quantity, dateOrder, idProduct, now, null, null, false);
        inventory.registerEvent(new InventoryCreated(inventory.id(), quantity, dateOrder, idProduct));
        inventory.validate();
        return inventory;
    }

    public static Inventory rehydrate(UUID id, Integer quantity, Instant dateOrder, UUID idProduct,
                                      Instant createdAt, Instant updatedAt, Instant deletedAt, boolean deleted) {
        var inventory = new Inventory(id, quantity, dateOrder, idProduct, createdAt, updatedAt, deletedAt, deleted);
        inventory.validate();
        return inventory;
    }

    public void update(Integer quantity, Instant dateOrder, UUID idProduct, Instant now) {
        this.quantity = quantity;
        this.dateOrder = dateOrder;
        this.idProduct = idProduct;
        touch(now);
        registerEvent(new InventoryUpdated(id(), quantity, dateOrder, idProduct));
        validate();
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();
        Checks.audit(id(), createdAt(), updatedAt(), deletedAt(), errors);
        Checks.provided(errors, quantity, "Quantity");
        Checks.positive(errors, quantity, "Quantity");
        Checks.provided(errors, dateOrder, "DateOrder");
        Checks.provided(errors, idProduct, "IdProduct");
        if (errors.isEmpty()) {
            markValid();
        } else {
            markInvalid(errors);
        }
    }

    public Integer quantity() {
        return quantity;
    }

    public Instant dateOrder() {
        return dateOrder;
    }

    public UUID idProduct() {
        return idProduct;
    }
}
