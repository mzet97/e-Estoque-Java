package io.github.mzet97.eestoque.tax.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

/** Tabela public."Taxs". Category é projeção somente leitura. */
@Entity
@Table(name = "\"Taxs\"", schema = "public")
public class TaxJpaEntity {

    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "Name", nullable = false, length = 80)
    private String name;

    @Column(name = "Description", nullable = false, length = 250)
    private String description;

    @Column(name = "Percentage", nullable = false)
    private BigDecimal percentage;

    @Column(name = "IdCategory", nullable = false, insertable = false, updatable = false)
    private UUID idCategory;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "IdCategory", nullable = false)
    private CategoryRefJpaEntity category;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UpdatedAt")
    private Instant updatedAt;

    @Column(name = "DeletedAt")
    private Instant deletedAt;

    @Column(name = "IsDeleted", nullable = false)
    private boolean deleted;

    protected TaxJpaEntity() {
    }

    public TaxJpaEntity(UUID id, String name, String description, BigDecimal percentage, UUID idCategory,
                        CategoryRefJpaEntity category, Instant createdAt, Instant updatedAt, Instant deletedAt,
                        boolean deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.percentage = percentage;
        this.idCategory = idCategory;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.deleted = deleted;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public UUID getIdCategory() {
        return idCategory;
    }

    public CategoryRefJpaEntity getCategory() {
        return category;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public void setCategory(CategoryRefJpaEntity category) {
        this.category = category;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

/** Projeção somente leitura da tabela public."Categories". */
@Entity
@Immutable
@Table(name = "\"Categories\"", schema = "public")
class CategoryRefJpaEntity {

    @Id
    @Column(name = "Id")
    private UUID id;

    @Column(name = "Name", insertable = false, updatable = false)
    private String name;

    @Column(name = "Description", insertable = false, updatable = false)
    private String description;

    @Column(name = "ShortDescription", insertable = false, updatable = false)
    private String shortDescription;

    @Column(name = "CreatedAt", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UpdatedAt", insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "DeletedAt", insertable = false, updatable = false)
    private Instant deletedAt;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}
