package io.github.mzet97.eestoque.inventory.infrastructure.persistence;

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

/**
 * Tabela public.inventories (minúsculas). DateOrder é varchar(80) no schema
 * original — persistido via DateOrderConverter (formato textual do Postgres).
 * Product é projeção somente leitura das tabelas Products/Categories/Companies.
 */
@Entity
@Table(name = "inventories", schema = "public")
class InventoryJpaEntity {

    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "DateOrder", nullable = false, length = 80)
    private String dateOrder;

    @Column(name = "IdProduct", nullable = false, insertable = false, updatable = false)
    private UUID idProduct;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "IdProduct", nullable = false)
    private ProductSnapshotJpaEntity product;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UpdatedAt")
    private Instant updatedAt;

    @Column(name = "DeletedAt")
    private Instant deletedAt;

    @Column(name = "IsDeleted", nullable = false)
    private boolean deleted;

    protected InventoryJpaEntity() {
    }

    public InventoryJpaEntity(UUID id, Integer quantity, String dateOrder, UUID idProduct,
                              ProductSnapshotJpaEntity product, Instant createdAt, Instant updatedAt,
                              Instant deletedAt, boolean deleted) {
        this.id = id;
        this.quantity = quantity;
        this.dateOrder = dateOrder;
        this.idProduct = idProduct;
        this.product = product;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.deleted = deleted;
    }

    public UUID getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public UUID getIdProduct() {
        return idProduct;
    }

    public ProductSnapshotJpaEntity getProduct() {
        return product;
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

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
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

/** Projeção somente leitura da tabela public."Products". */
@Entity
@Immutable
@Table(name = "\"Products\"", schema = "public")
class ProductSnapshotJpaEntity {

    @Id
    @Column(name = "Id")
    private UUID id;

    @Column(name = "Name", insertable = false, updatable = false)
    private String name;

    @Column(name = "Description", insertable = false, updatable = false)
    private String description;

    @Column(name = "ShortDescription", insertable = false, updatable = false)
    private String shortDescription;

    @Column(name = "Price", insertable = false, updatable = false)
    private java.math.BigDecimal price;

    @Column(name = "Weight", insertable = false, updatable = false)
    private java.math.BigDecimal weight;

    @Column(name = "Height", insertable = false, updatable = false)
    private java.math.BigDecimal height;

    @Column(name = "Length", insertable = false, updatable = false)
    private java.math.BigDecimal length;

    @Column(name = "Image", insertable = false, updatable = false)
    private String image;

    @Column(name = "IdCategory", insertable = false, updatable = false)
    private UUID idCategory;

    @Column(name = "IdCompany", insertable = false, updatable = false)
    private UUID idCompany;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IdCategory", insertable = false, updatable = false)
    private CategorySnapshotJpaEntity category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IdCompany", insertable = false, updatable = false)
    private CompanySnapshotJpaEntity company;

    @Column(name = "CreatedAt", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UpdatedAt", insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "DeletedAt", insertable = false, updatable = false)
    private Instant deletedAt;

    @Column(name = "IsDeleted", insertable = false, updatable = false)
    private boolean deleted;

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

    public java.math.BigDecimal getPrice() {
        return price;
    }

    public java.math.BigDecimal getWeight() {
        return weight;
    }

    public java.math.BigDecimal getHeight() {
        return height;
    }

    public java.math.BigDecimal getLength() {
        return length;
    }

    public String getImage() {
        return image;
    }

    public UUID getIdCategory() {
        return idCategory;
    }

    public UUID getIdCompany() {
        return idCompany;
    }

    public CategorySnapshotJpaEntity getCategory() {
        return category;
    }

    public CompanySnapshotJpaEntity getCompany() {
        return company;
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
}

/** Projeção somente leitura da tabela public."Categories". */
@Entity
@Immutable
@Table(name = "\"Categories\"", schema = "public")
class CategorySnapshotJpaEntity {

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

/** Projeção somente leitura da tabela public."Companies". */
@Entity
@Immutable
@Table(name = "\"Companies\"", schema = "public")
class CompanySnapshotJpaEntity {

    @Id
    @Column(name = "Id")
    private UUID id;

    @Column(name = "Name", insertable = false, updatable = false)
    private String name;

    @Column(name = "DocId", insertable = false, updatable = false)
    private String docId;

    @Column(name = "Email", insertable = false, updatable = false)
    private String email;

    @Column(name = "Description", insertable = false, updatable = false)
    private String description;

    @Column(name = "PhoneNumber", insertable = false, updatable = false)
    private String phoneNumber;

    @Column(name = "CompanyAddress_Street", insertable = false, updatable = false)
    private String addressStreet;

    @Column(name = "CompanyAddress_Number", insertable = false, updatable = false)
    private String addressNumber;

    @Column(name = "CompanyAddress_Complement", insertable = false, updatable = false)
    private String addressComplement;

    @Column(name = "CompanyAddress_Neighborhood", insertable = false, updatable = false)
    private String addressNeighborhood;

    @Column(name = "CompanyAddress_District", insertable = false, updatable = false)
    private String addressDistrict;

    @Column(name = "CompanyAddress_City", insertable = false, updatable = false)
    private String addressCity;

    @Column(name = "CompanyAddress_County", insertable = false, updatable = false)
    private String addressCountry;

    @Column(name = "CompanyAddress_ZipCode", insertable = false, updatable = false)
    private String addressZipCode;

    @Column(name = "CompanyAddress_Latitude", insertable = false, updatable = false)
    private String addressLatitude;

    @Column(name = "CompanyAddress_Longitude", insertable = false, updatable = false)
    private String addressLongitude;

    @Column(name = "CreatedAt", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UpdatedAt", insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "DeletedAt", insertable = false, updatable = false)
    private Instant deletedAt;

    @Column(name = "IsDeleted", insertable = false, updatable = false)
    private boolean deleted;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDocId() {
        return docId;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public String getAddressComplement() {
        return addressComplement;
    }

    public String getAddressNeighborhood() {
        return addressNeighborhood;
    }

    public String getAddressDistrict() {
        return addressDistrict;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public String getAddressZipCode() {
        return addressZipCode;
    }

    public String getAddressLatitude() {
        return addressLatitude;
    }

    public String getAddressLongitude() {
        return addressLongitude;
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
}
