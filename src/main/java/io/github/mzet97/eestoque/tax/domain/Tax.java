package io.github.mzet97.eestoque.tax.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.Entity;
import io.github.mzet97.eestoque.shared.domain.validation.Checks;

/**
 * Imposto por categoria. Invariantes do TaxValidation .NET: Name 3–80,
 * Description 3–250, Percentage 0–100 (NotEmpty + faixa, como o original:
 * 0% falha em "needs to be provided").
 */
public final class Tax extends Entity {

    private String name;
    private String description;
    private BigDecimal percentage;
    private UUID idCategory;

    private Tax(UUID id, String name, String description, BigDecimal percentage, UUID idCategory, Instant createdAt,
                Instant updatedAt, Instant deletedAt, boolean deleted) {
        super(id, createdAt, updatedAt, deletedAt, deleted);
        this.name = name;
        this.description = description;
        this.percentage = percentage;
        this.idCategory = idCategory;
    }

    public static Tax create(String name, String description, BigDecimal percentage, UUID idCategory, Instant now) {
        var tax = new Tax(UUID.randomUUID(), name, description, percentage, idCategory, now, null, null, false);
        tax.registerEvent(new TaxCreated(tax.id(), name, description, percentage, idCategory));
        tax.validate();
        return tax;
    }

    public static Tax rehydrate(UUID id, String name, String description, BigDecimal percentage, UUID idCategory,
                                Instant createdAt, Instant updatedAt, Instant deletedAt, boolean deleted) {
        var tax = new Tax(id, name, description, percentage, idCategory, createdAt, updatedAt, deletedAt, deleted);
        tax.validate();
        return tax;
    }

    public void update(String name, String description, BigDecimal percentage, UUID idCategory, Instant now) {
        this.name = name;
        this.description = description;
        this.percentage = percentage;
        this.idCategory = idCategory;
        touch(now);
        registerEvent(new TaxUpdated(id(), name, description, percentage, idCategory));
        validate();
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();
        Checks.audit(id(), createdAt(), updatedAt(), deletedAt(), errors);
        Checks.provided(errors, name, "Name");
        Checks.length(errors, name, "Name", 3, 80);
        Checks.provided(errors, description, "Description");
        Checks.length(errors, description, "Description", 3, 250);
        Checks.provided(errors, percentage, "Percentage");
        Checks.inclusiveRange(errors, percentage, "Percentage", 0, 100);
        if (errors.isEmpty()) {
            markValid();
        } else {
            markInvalid(errors);
        }
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public BigDecimal percentage() {
        return percentage;
    }

    public UUID idCategory() {
        return idCategory;
    }
}
