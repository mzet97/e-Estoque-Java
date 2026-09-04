package io.github.mzet97.eestoque.product.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.validation.Checks;
import io.github.mzet97.eestoque.shared.domain.Entity;

/**
 * Produto (aggregate root). Invariantes do ProductValidation .NET na ordem
 * original: Name 3–250, Description 3–500, ShortDescription 3–250,
 * Price/Height/Weight/Length > 0, Image 3–5000, IdCategory/IdCompany.
 */
public final class Product extends Entity {

    private String name;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal weight;
    private BigDecimal height;
    private BigDecimal length;
    private String image;
    private UUID idCategory;
    private UUID idCompany;

    private Product(UUID id, String name, String description, String shortDescription, BigDecimal price,
                    BigDecimal weight, BigDecimal height, BigDecimal length, String image, UUID idCategory,
                    UUID idCompany, Instant createdAt, Instant updatedAt, Instant deletedAt, boolean deleted) {
        super(id, createdAt, updatedAt, deletedAt, deleted);
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
        this.price = price;
        this.weight = weight;
        this.height = height;
        this.length = length;
        this.image = image;
        this.idCategory = idCategory;
        this.idCompany = idCompany;
    }

    public static Product create(String name, String description, String shortDescription, BigDecimal price,
                                 BigDecimal weight, BigDecimal height, BigDecimal length, String image,
                                 UUID idCategory, UUID idCompany, Instant now) {
        var product = new Product(UUID.randomUUID(), name, description, shortDescription, price, weight, height,
                length, image, idCategory, idCompany, now, null, null, false);
        product.registerEvent(new ProductCreated(product.id(), name, description, shortDescription, price, weight,
                height, length, idCategory, idCompany));
        product.validate();
        return product;
    }

    public static Product rehydrate(UUID id, String name, String description, String shortDescription,
                                    BigDecimal price, BigDecimal weight, BigDecimal height, BigDecimal length,
                                    String image, UUID idCategory, UUID idCompany, Instant createdAt,
                                    Instant updatedAt, Instant deletedAt, boolean deleted) {
        var product = new Product(id, name, description, shortDescription, price, weight, height, length, image,
                idCategory, idCompany, createdAt, updatedAt, deletedAt, deleted);
        product.validate();
        return product;
    }

    public void update(String name, String description, String shortDescription, BigDecimal price, BigDecimal weight,
                       BigDecimal height, BigDecimal length, String image, UUID idCategory, UUID idCompany,
                       Instant now) {
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
        this.price = price;
        this.weight = weight;
        this.height = height;
        this.length = length;
        this.image = image;
        this.idCategory = idCategory;
        this.idCompany = idCompany;
        touch(now);
        registerEvent(new ProductUpdated(id(), name, description, shortDescription, price, weight, height, length,
                image, idCategory, idCompany));
        validate();
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();
        Checks.audit(id(), createdAt(), updatedAt(), deletedAt(), errors);
        Checks.provided(errors, name, "Name");
        Checks.length(errors, name, "Name", 3, 250);
        Checks.provided(errors, description, "Description");
        Checks.length(errors, description, "Description", 3, 500);
        Checks.provided(errors, shortDescription, "ShortDescription");
        Checks.length(errors, shortDescription, "ShortDescription", 3, 250);
        Checks.provided(errors, price, "Price");
        Checks.positiveDecimal(errors, price, "Price");
        Checks.provided(errors, height, "Height");
        Checks.positiveDecimal(errors, height, "Height");
        Checks.provided(errors, weight, "Weight");
        Checks.positiveDecimal(errors, weight, "Weight");
        Checks.provided(errors, length, "Length");
        Checks.positiveDecimal(errors, length, "Length");
        Checks.provided(errors, image, "Image");
        Checks.length(errors, image, "Image", 3, 5000);
        Checks.provided(errors, idCategory, "IdCategory");
        Checks.provided(errors, idCompany, "IdCompany");
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

    public BigDecimal price() {
        return price;
    }

    public BigDecimal weight() {
        return weight;
    }

    public BigDecimal height() {
        return height;
    }

    public BigDecimal length() {
        return length;
    }

    public String image() {
        return image;
    }

    public UUID idCategory() {
        return idCategory;
    }

    public UUID idCompany() {
        return idCompany;
    }
}
