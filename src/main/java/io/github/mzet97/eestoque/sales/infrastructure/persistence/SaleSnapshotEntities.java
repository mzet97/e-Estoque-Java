package io.github.mzet97.eestoque.sales.infrastructure.persistence;

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

/** Projeções somente leitura de Products/Categories/Companies para Sales. */
@Entity(name = "SalesProductSnapshot")
@Immutable
@Table(name = "Products", schema = "public")
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
    private BigDecimal price;

    @Column(name = "Weight", insertable = false, updatable = false)
    private BigDecimal weight;

    @Column(name = "Height", insertable = false, updatable = false)
    private BigDecimal height;

    @Column(name = "Length", insertable = false, updatable = false)
    private BigDecimal length;

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

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public BigDecimal getLength() {
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

@Entity(name = "SalesCategorySnapshot")
@Immutable
@Table(name = "Categories", schema = "public")
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

@Entity(name = "SalesCompanySnapshot")
@Immutable
@Table(name = "Companies", schema = "public")
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
