package io.github.mzet97.eestoque.product.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Mapeamento JPA da tabela public."Categories" (schema .NET preservado,
 * colunas PascalCase).
 */
@Entity
@Table(name = "Categories", schema = "public")
public class CategoryJpaEntity {

    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "Name", nullable = false, length = 80)
    private String name;

    @Column(name = "Description", nullable = false, length = 5000)
    private String description;

    @Column(name = "ShortDescription", nullable = false, length = 500)
    private String shortDescription;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UpdatedAt")
    private Instant updatedAt;

    @Column(name = "DeletedAt")
    private Instant deletedAt;

    @Column(name = "IsDeleted", nullable = false)
    private boolean deleted;

    protected CategoryJpaEntity() {
    }

    public CategoryJpaEntity(UUID id, String name, String description, String shortDescription,
                             Instant createdAt, Instant updatedAt, Instant deletedAt, boolean deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
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
