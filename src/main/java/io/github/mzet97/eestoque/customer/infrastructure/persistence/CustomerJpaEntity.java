package io.github.mzet97.eestoque.customer.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Tabela public."Customers" — endereço embutido com colunas
 * CustomerAddress_* (incluindo o typo _County de Country); Email varchar(80).
 */
@Entity
@Table(name = "Customers", schema = "public")
public class CustomerJpaEntity {

    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "Name", nullable = false, length = 80)
    private String name;

    @Column(name = "DocId", nullable = false, length = 80)
    private String docId;

    @Column(name = "Email", nullable = false, length = 80)
    private String email;

    @Column(name = "Description", nullable = false, length = 250)
    private String description;

    @Column(name = "PhoneNumber", nullable = false, length = 80)
    private String phoneNumber;

    @Column(name = "CustomerAddress_Street", nullable = false, length = 80)
    private String addressStreet;

    @Column(name = "CustomerAddress_Number", nullable = false, length = 80)
    private String addressNumber;

    @Column(name = "CustomerAddress_Complement", nullable = false, length = 80)
    private String addressComplement;

    @Column(name = "CustomerAddress_Neighborhood", nullable = false, length = 80)
    private String addressNeighborhood;

    @Column(name = "CustomerAddress_District", nullable = false, length = 80)
    private String addressDistrict;

    @Column(name = "CustomerAddress_City", nullable = false, length = 80)
    private String addressCity;

    @Column(name = "CustomerAddress_County", nullable = false, length = 80)
    private String addressCountry;

    @Column(name = "CustomerAddress_ZipCode", nullable = false, length = 80)
    private String addressZipCode;

    @Column(name = "CustomerAddress_Latitude", nullable = false, length = 80)
    private String addressLatitude;

    @Column(name = "CustomerAddress_Longitude", nullable = false, length = 80)
    private String addressLongitude;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UpdatedAt")
    private Instant updatedAt;

    @Column(name = "DeletedAt")
    private Instant deletedAt;

    @Column(name = "IsDeleted", nullable = false)
    private boolean deleted;

    protected CustomerJpaEntity() {
    }

    public CustomerJpaEntity(UUID id, String name, String docId, String email, String description, String phoneNumber,
                             String addressStreet, String addressNumber, String addressComplement,
                             String addressNeighborhood, String addressDistrict, String addressCity,
                             String addressCountry, String addressZipCode, String addressLatitude,
                             String addressLongitude, Instant createdAt, Instant updatedAt, Instant deletedAt,
                             boolean deleted) {
        this.id = id;
        this.name = name;
        this.docId = docId;
        this.email = email;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.addressStreet = addressStreet;
        this.addressNumber = addressNumber;
        this.addressComplement = addressComplement;
        this.addressNeighborhood = addressNeighborhood;
        this.addressDistrict = addressDistrict;
        this.addressCity = addressCity;
        this.addressCountry = addressCountry;
        this.addressZipCode = addressZipCode;
        this.addressLatitude = addressLatitude;
        this.addressLongitude = addressLongitude;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public void setAddressComplement(String addressComplement) {
        this.addressComplement = addressComplement;
    }

    public void setAddressNeighborhood(String addressNeighborhood) {
        this.addressNeighborhood = addressNeighborhood;
    }

    public void setAddressDistrict(String addressDistrict) {
        this.addressDistrict = addressDistrict;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public void setAddressZipCode(String addressZipCode) {
        this.addressZipCode = addressZipCode;
    }

    public void setAddressLatitude(String addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public void setAddressLongitude(String addressLongitude) {
        this.addressLongitude = addressLongitude;
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
