package io.github.mzet97.eestoque.sales.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.Entity;
import io.github.mzet97.eestoque.shared.domain.validation.Checks;

/** Item da venda. Invariantes do SaleProductValidation .NET. */
public final class SaleProduct extends Entity {

    private Integer quantity;
    private UUID idProduct;
    private UUID idSale;

    private SaleProduct(UUID id, Instant createdAt, Instant updatedAt, Instant deletedAt, boolean deleted) {
        super(id, createdAt, updatedAt, deletedAt, deleted);
    }

    public SaleProduct(UUID id, Integer quantity, UUID idProduct, UUID idSale, Instant now) {
        super(id, now, null, null, false);
        this.quantity = quantity;
        this.idProduct = idProduct;
        this.idSale = idSale;
    }

    public static SaleProduct rehydrate(UUID id, Integer quantity, UUID idProduct, UUID idSale, Instant createdAt,
                                        Instant updatedAt, Instant deletedAt, boolean deleted) {
        var saleProduct = new SaleProduct(id, createdAt, updatedAt, deletedAt, deleted);
        saleProduct.quantity = quantity;
        saleProduct.idProduct = idProduct;
        saleProduct.idSale = idSale;
        saleProduct.validate();
        return saleProduct;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();
        Checks.audit(id(), createdAt(), updatedAt(), deletedAt(), errors);
        Checks.notEmptyGuid(errors, idProduct, "IdProduct");
        Checks.notEmptyGuid(errors, idSale, "IdSale");
        if (errors.isEmpty()) {
            markValid();
        } else {
            markInvalid(errors);
        }
    }

    public Integer quantity() {
        return quantity;
    }

    public UUID idProduct() {
        return idProduct;
    }

    public UUID idSale() {
        return idSale;
    }
}
