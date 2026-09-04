package io.github.mzet97.eestoque.company.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.domain.Entity;
import io.github.mzet97.eestoque.shared.domain.validation.Checks;

/**
 * Empresa. Invariantes do CompanyValidation .NET: Name/DocId/PhoneNumber
 * 3–80; Email/Description 3–250; endereço completo obrigatório (3–80).
 */
public final class Company extends Entity {

    private String name;
    private String docId;
    private String email;
    private String description;
    private String phoneNumber;
    private Address address;

    private Company(UUID id, String name, String docId, String email, String description, String phoneNumber,
                    Address address, Instant createdAt, Instant updatedAt, Instant deletedAt, boolean deleted) {
        super(id, createdAt, updatedAt, deletedAt, deleted);
        this.name = name;
        this.docId = docId;
        this.email = email;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public static Company create(String name, String docId, String email, String description, String phoneNumber,
                                 Address address, Instant now) {
        var company = new Company(UUID.randomUUID(), name, docId, email, description, phoneNumber, address,
                now, null, null, false);
        company.registerEvent(new CompanyCreated(company.id(), name, docId, email, description, phoneNumber, address));
        company.validate();
        return company;
    }

    public static Company rehydrate(UUID id, String name, String docId, String email, String description,
                                    String phoneNumber, Address address, Instant createdAt, Instant updatedAt,
                                    Instant deletedAt, boolean deleted) {
        var company = new Company(id, name, docId, email, description, phoneNumber, address,
                createdAt, updatedAt, deletedAt, deleted);
        company.validate();
        return company;
    }

    public void update(String name, String docId, String email, String description, String phoneNumber,
                       Address address, Instant now) {
        this.name = name;
        this.docId = docId;
        this.email = email;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.address = address;
        touch(now);
        registerEvent(new CompanyUpdated(id(), name, docId, email, description, phoneNumber, address));
        validate();
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();
        Checks.audit(id(), createdAt(), updatedAt(), deletedAt(), errors);
        Checks.required(errors, name, "Name");
        Checks.length(errors, name, "Name", 3, 80);
        Checks.required(errors, docId, "DocId");
        Checks.length(errors, docId, "DocId", 3, 80);
        Checks.required(errors, email, "Email");
        Checks.length(errors, email, "Email", 3, 250);
        Checks.required(errors, description, "Description");
        Checks.length(errors, description, "Description", 3, 250);
        Checks.required(errors, phoneNumber, "PhoneNumber");
        Checks.length(errors, phoneNumber, "PhoneNumber", 3, 80);
        Checks.required(errors, (Object) address, "CompanyAddress");
        checkAddress(errors);
        if (errors.isEmpty()) {
            markValid();
        } else {
            markInvalid(errors);
        }
    }

    private void checkAddress(List<String> errors) {
        if (address == null) {
            return;
        }
        Checks.required(errors, address.street(), "Street");
        Checks.length(errors, address.street(), "Street", 3, 80);
        Checks.required(errors, address.number(), "Number");
        Checks.length(errors, address.number(), "Number", 3, 80);
        Checks.required(errors, address.complement(), "Complement");
        Checks.length(errors, address.complement(), "Complement", 3, 80);
        Checks.required(errors, address.neighborhood(), "Neighborhood");
        Checks.length(errors, address.neighborhood(), "Neighborhood", 3, 80);
        Checks.required(errors, address.district(), "District");
        Checks.length(errors, address.district(), "District", 3, 80);
        Checks.required(errors, address.city(), "City");
        Checks.length(errors, address.city(), "City", 3, 80);
        Checks.required(errors, address.country(), "Country");
        Checks.length(errors, address.country(), "Country", 3, 80);
        Checks.required(errors, address.zipCode(), "ZipCode");
        Checks.length(errors, address.zipCode(), "ZipCode", 3, 80);
        Checks.required(errors, address.latitude(), "Latitude");
        Checks.length(errors, address.latitude(), "Latitude", 3, 80);
    }

    public String name() {
        return name;
    }

    public String docId() {
        return docId;
    }

    public String email() {
        return email;
    }

    public String description() {
        return description;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public Address address() {
        return address;
    }
}
