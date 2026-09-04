package io.github.mzet97.eestoque.product.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.Entity;
import io.github.mzet97.eestoque.shared.domain.validation.Checks;

/**
 * Categoria de produtos. Invariantes com as mensagens do CategoryValidation
 * .NET: Name 3–80, ShortDescription 3–500, Description 3–5000.
 */
public final class Category extends Entity {

    private String name;
    private String description;
    private String shortDescription;

    private Category(UUID id, String name, String description, String shortDescription,
                     Instant createdAt, Instant updatedAt, Instant deletedAt, boolean deleted) {
        super(id, createdAt, updatedAt, deletedAt, deleted);
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
    }

    public static Category create(String name, String description, String shortDescription, Instant now) {
        var category = new Category(UUID.randomUUID(), name, description, shortDescription, now, null, null, false);
        category.registerEvent(new CategoryCreated(category.id(), name, description, shortDescription));
        category.validate();
        return category;
    }

    /** Reidratação a partir da persistência (não gera eventos). */
    public static Category rehydrate(UUID id, String name, String description, String shortDescription,
                                     Instant createdAt, Instant updatedAt, Instant deletedAt, boolean deleted) {
        var category = new Category(id, name, description, shortDescription, createdAt, updatedAt, deletedAt, deleted);
        category.validate();
        return category;
    }

    public void update(String name, String description, String shortDescription, Instant now) {
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
        touch(now);
        registerEvent(new CategoryUpdated(id(), name, description, shortDescription));
        validate();
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();
        Checks.audit(id(), createdAt(), updatedAt(), deletedAt(), errors);
        Checks.provided(errors, name, "Name");
        Checks.length(errors, name, "Name", 3, 80);
        Checks.provided(errors, shortDescription, "ShortDescription");
        Checks.length(errors, shortDescription, "ShortDescription", 3, 500);
        Checks.provided(errors, description, "Description");
        Checks.length(errors, description, "Description", 3, 5000);
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

    public String shortDescription() {
        return shortDescription;
    }
}
