package io.github.mzet97.eestoque.sales.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.Entity;
import io.github.mzet97.eestoque.shared.domain.validation.Checks;

/**
 * Venda (aggregate root) com itens SaleProduct. Como no .NET: totalPrice e
 * totalTax vêm do cliente (sem cálculo no servidor), cada item recebe a
 * MESMA quantity e a venda NÃO baixa estoque. Delete é soft (disable).
 */
public final class Sale extends Entity {

    private Integer quantity;
    private BigDecimal totalPrice;
    private BigDecimal totalTax;
    private SaleType saleType;
    private PaymentType paymentType;
    private Instant deliveryDate;
    private Instant saleDate;
    private Instant paymentDate;
    private UUID idCustomer;
    private final List<SaleProduct> saleProducts = new ArrayList<>();

    private Sale(UUID id, Instant createdAt, Instant updatedAt, Instant deletedAt, boolean deleted) {
        super(id, createdAt, updatedAt, deletedAt, deleted);
    }

    public static Sale create(Integer quantity, BigDecimal totalPrice, BigDecimal totalTax, SaleType saleType,
                              PaymentType paymentType, Instant deliveryDate, Instant saleDate, Instant paymentDate,
                              UUID idCustomer, List<UUID> idsProducts, Instant now) {
        var sale = new Sale(UUID.randomUUID(), now, null, null, false);
        fill(sale, quantity, totalPrice, totalTax, saleType, paymentType, deliveryDate, saleDate, paymentDate,
                idCustomer, idsProducts, now);

        sale.registerEvent(new SaleCreated(sale.id(), sale.quantity, sale.totalPrice, sale.totalTax,
                sale.saleType == null ? 0 : sale.saleType.toInt(),
                sale.paymentType == null ? 0 : sale.paymentType.toInt(),
                deliveryDate, saleDate, paymentDate, idCustomer,
                idsProducts.stream().map(id -> (UUID) null).toList()));
        sale.validate();
        return sale;
    }

    public static Sale rehydrate(UUID id, Integer quantity, BigDecimal totalPrice, BigDecimal totalTax,
                                 SaleType saleType, PaymentType paymentType, Instant deliveryDate, Instant saleDate,
                                 Instant paymentDate, UUID idCustomer, List<SaleProduct> saleProducts,
                                 Instant createdAt, Instant updatedAt, Instant deletedAt, boolean deleted) {
        var sale = new Sale(id, createdAt, updatedAt, deletedAt, deleted);
        sale.quantity = quantity;
        sale.totalPrice = totalPrice;
        sale.totalTax = totalTax;
        sale.saleType = saleType;
        sale.paymentType = paymentType;
        sale.deliveryDate = deliveryDate;
        sale.saleDate = saleDate;
        sale.paymentDate = paymentDate;
        sale.idCustomer = idCustomer;
        sale.saleProducts.addAll(saleProducts);
        sale.validate();
        return sale;
    }

    public void update(Integer quantity, BigDecimal totalPrice, BigDecimal totalTax, SaleType saleType,
                       PaymentType paymentType, Instant deliveryDate, Instant saleDate, Instant paymentDate,
                       UUID idCustomer, List<UUID> idsProducts, Instant now) {
        fill(this, quantity, totalPrice, totalTax, saleType, paymentType, deliveryDate, saleDate, paymentDate,
                idCustomer, idsProducts, now);
        touch(now);
        registerEvent(new SaleUpdated(id(), this.quantity, this.totalPrice, this.totalTax,
                this.saleType == null ? 0 : this.saleType.toInt(),
                this.paymentType == null ? 0 : this.paymentType.toInt(),
                deliveryDate, saleDate, paymentDate, idCustomer,
                idsProducts.stream().map(id -> (UUID) null).toList()));
        validate();
    }

    private static void fill(Sale sale, Integer quantity, BigDecimal totalPrice, BigDecimal totalTax,
                             SaleType saleType, PaymentType paymentType, Instant deliveryDate, Instant saleDate,
                             Instant paymentDate, UUID idCustomer, List<UUID> idsProducts, Instant now) {
        sale.quantity = quantity;
        sale.totalPrice = totalPrice;
        sale.totalTax = totalTax;
        sale.saleType = saleType;
        sale.paymentType = paymentType;
        sale.deliveryDate = deliveryDate;
        sale.saleDate = saleDate;
        sale.paymentDate = paymentDate;
        sale.idCustomer = idCustomer;
        sale.saleProducts.clear();
        // Como no .NET: um SaleProduct por id, todos com a mesma quantity.
        for (UUID idProduct : idsProducts == null ? List.<UUID>of() : idsProducts) {
            sale.saleProducts.add(new SaleProduct(UUID.randomUUID(), quantity, idProduct, sale.id(), now));
        }
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();
        Checks.audit(id(), createdAt(), updatedAt(), deletedAt(), errors);
        Checks.provided(errors, quantity, "Quantity");
        Checks.positive(errors, quantity, "Quantity");
        Checks.provided(errors, saleType, "SaleType");
        Checks.provided(errors, paymentType, "PaymentType");
        Checks.provided(errors, totalPrice, "TotalPrice");
        Checks.positiveDecimal(errors, totalPrice, "TotalPrice");
        Checks.provided(errors, totalTax, "TotalTax");
        Checks.positiveDecimal(errors, totalTax, "TotalTax");
        Checks.provided(errors, deliveryDate, "DeliveryDate");
        Checks.provided(errors, saleDate, "SaleDate");
        Checks.provided(errors, paymentDate, "PaymentDate");
        Checks.provided(errors, idCustomer, "IdCustomer");
        if (errors.isEmpty()) {
            markValid();
        } else {
            markInvalid(errors);
        }
    }

    public Integer quantity() {
        return quantity;
    }

    public BigDecimal totalPrice() {
        return totalPrice;
    }

    public BigDecimal totalTax() {
        return totalTax;
    }

    public SaleType saleType() {
        return saleType;
    }

    public PaymentType paymentType() {
        return paymentType;
    }

    public Instant deliveryDate() {
        return deliveryDate;
    }

    public Instant saleDate() {
        return saleDate;
    }

    public Instant paymentDate() {
        return paymentDate;
    }

    public UUID idCustomer() {
        return idCustomer;
    }

    public List<SaleProduct> saleProducts() {
        return List.copyOf(saleProducts);
    }
}
