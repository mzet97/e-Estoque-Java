package io.github.mzet97.eestoque.product.infrastructure.persistence;

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

/**
 * Tabela public."Products". Category pertence ao próprio módulo; Company é
 * uma projeção somente leitura da tabela "Companies" (fronteira de módulo).
 * EAGER reproduz os Include obrigatórios do .NET (GetById/Search/Gridify).
 */
@Entity
@Table(name = "\"Products\"", schema = "public")
class ProductJpaEntity {

    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "Name", nullable = false, columnDefinition = "text")
    private String name;

    @Column(name = "Description", nullable = false, length = 500)
    private String description;

    @Column(name = "ShortDescription", nullable = false, length = 250)
    private String shortDescription;

    @Column(name = "Price", nullable = false)
    private BigDecimal price;

    @Column(name = "Weight", nullable = false)
    private BigDecimal weight;

    @Column(name = "Height", nullable = false)
    private BigDecimal height;

    @Column(name = "Length", nullable = false)
    private BigDecimal length;

    @Column(name = "Image", nullable = false, length = 5000)
    private String image;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "IdCategory", nullable = false)
    private CategoryJpaEntity category;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "IdCompany", nullable = false)
    private CompanySummaryJpaEntity company;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UpdatedAt")
    private Instant updatedAt;

    @Column(name = "DeletedAt")
    private Instant deletedAt;

    @Column(name = "IsDeleted", nullable = false)
    private boolean deleted;

    protected ProductJpaEntity() {
    }

    public ProductJpaEntity(UUID id, String name, String description, String shortDescription, BigDecimal price,
                            BigDecimal weight, BigDecimal height, BigDecimal length, String image,
                            CategoryJpaEntity category, CompanySummaryJpaEntity company, Instant createdAt,
                            Instant updatedAt, Instant deletedAt, boolean deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
        this.price = price;
        this.weight = weight;
        this.height = height;
        this.length = length;
        this.image = image;
        this.category = category;
        this.company = company;
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

    public CategoryJpaEntity getCategory() {
        return category;
    }

    public CompanySummaryJpaEntity getCompany() {
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCategory(CategoryJpaEntity category) {
        this.category = category;
    }

    public void setCompany(CompanySummaryJpaEntity company) {
        this.company = company;
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

/**
 * Projeção somente leitura da tabela public."Companies" para o módulo
 * product (mesma estratégia de um futuro serviço independente).
 */
@Entity
@Immutable
@Table(name = "\"Companies\"", schema = "public")
class CompanySummaryJpaEntity {

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
