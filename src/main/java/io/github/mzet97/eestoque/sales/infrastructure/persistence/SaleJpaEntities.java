package io.github.mzet97.eestoque.sales.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

/** Tabela public."Sales". */
@Entity
@Table(name = "Sales", schema = "public")
class SaleJpaEntity {

    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "TotalPrice", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "TotalTax", nullable = false)
    private BigDecimal totalTax;

    @Column(name = "SaleType", nullable = false)
    private Integer saleType;

    @Column(name = "PaymentType", nullable = false)
    private Integer paymentType;

    @Column(name = "DeliveryDate", nullable = false)
    private Instant deliveryDate;

    @Column(name = "SaleDate", nullable = false)
    private Instant saleDate;

    @Column(name = "PaymentDate", nullable = false)
    private Instant paymentDate;

    @Column(name = "IdCustomer", nullable = false)
    private UUID idCustomer;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SaleProductJpaEntity> saleProducts = new ArrayList<>();

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UpdatedAt")
    private Instant updatedAt;

    @Column(name = "DeletedAt")
    private Instant deletedAt;

    @Column(name = "IsDeleted", nullable = false)
    private boolean deleted;

    protected SaleJpaEntity() {
    }

    SaleJpaEntity(UUID id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public Instant getDeliveryDate() {
        return deliveryDate;
    }

    public Instant getSaleDate() {
        return saleDate;
    }

    public Instant getPaymentDate() {
        return paymentDate;
    }

    public UUID getIdCustomer() {
        return idCustomer;
    }

    public List<SaleProductJpaEntity> getSaleProducts() {
        return saleProducts;
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

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public void setDeliveryDate(Instant deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setSaleDate(Instant saleDate) {
        this.saleDate = saleDate;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setIdCustomer(UUID idCustomer) {
        this.idCustomer = idCustomer;
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

/** Tabela public."SaleProducts". */
@Entity
@Table(name = "SaleProducts", schema = "public")
class SaleProductJpaEntity {

    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "IdProduct", nullable = false, insertable = false, updatable = false)
    private UUID idProduct;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IdProduct", nullable = false)
    private ProductSnapshotJpaEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdSale", nullable = false)
    private SaleJpaEntity sale;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UpdatedAt")
    private Instant updatedAt;

    @Column(name = "DeletedAt")
    private Instant deletedAt;

    @Column(name = "IsDeleted", nullable = false)
    private boolean deleted;

    protected SaleProductJpaEntity() {
    }

    SaleProductJpaEntity(UUID id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public UUID getIdProduct() {
        return idProduct;
    }

    public ProductSnapshotJpaEntity getProduct() {
        return product;
    }

    public SaleJpaEntity getSale() {
        return sale;
    }

    public void setSale(SaleJpaEntity sale) {
        this.sale = sale;
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

    public void setProduct(ProductSnapshotJpaEntity product) {
        this.product = product;
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

/** Projeção somente leitura da tabela public."Customers". */
@Entity
@Immutable
@Table(name = "Customers", schema = "public")
class CustomerSnapshotJpaEntity {

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

    @Column(name = "CustomerAddress_Street", insertable = false, updatable = false)
    private String addressStreet;

    @Column(name = "CustomerAddress_Number", insertable = false, updatable = false)
    private String addressNumber;

    @Column(name = "CustomerAddress_Complement", insertable = false, updatable = false)
    private String addressComplement;

    @Column(name = "CustomerAddress_Neighborhood", insertable = false, updatable = false)
    private String addressNeighborhood;

    @Column(name = "CustomerAddress_District", insertable = false, updatable = false)
    private String addressDistrict;

    @Column(name = "CustomerAddress_City", insertable = false, updatable = false)
    private String addressCity;

    @Column(name = "CustomerAddress_County", insertable = false, updatable = false)
    private String addressCountry;

    @Column(name = "CustomerAddress_ZipCode", insertable = false, updatable = false)
    private String addressZipCode;

    @Column(name = "CustomerAddress_Latitude", insertable = false, updatable = false)
    private String addressLatitude;

    @Column(name = "CustomerAddress_Longitude", insertable = false, updatable = false)
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
