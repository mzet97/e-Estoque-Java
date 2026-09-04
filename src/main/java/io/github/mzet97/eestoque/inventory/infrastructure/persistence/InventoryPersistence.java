package io.github.mzet97.eestoque.inventory.infrastructure.persistence;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.inventory.domain.Inventory;
import io.github.mzet97.eestoque.inventory.domain.InventoryCriteria;
import io.github.mzet97.eestoque.inventory.domain.InventoryRepository;
import io.github.mzet97.eestoque.inventory.domain.InventoryViewData;
import io.github.mzet97.eestoque.inventory.domain.ProductReader;
import io.github.mzet97.eestoque.inventory.domain.ProductSnapshot;
import io.github.mzet97.eestoque.shared.application.PagedResult;
import io.github.mzet97.eestoque.shared.application.SearchResult;
import io.github.mzet97.eestoque.shared.infrastructure.persistence.EntityFieldMaps;
import io.github.mzet97.eestoque.shared.infrastructure.persistence.SpecificationSearch;

interface InventorySpringDataRepository
        extends JpaRepository<InventoryJpaEntity, UUID>, JpaSpecificationExecutor<InventoryJpaEntity> {
}

interface ProductSnapshotSpringDataRepository extends JpaRepository<ProductSnapshotJpaEntity, UUID> {
}

/**
 * Conversor do quirk do schema original: coluna DateOrder é varchar(80).
 * Grava no formato textual do Postgres ("yyyy-MM-dd HH:mm:ss", UTC) e lê
 * tanto nesse formato quanto em ISO-8601.
 */
@Converter
class DateOrderConverter implements AttributeConverter<Instant, String> {

    static final DateTimeFormatter WRITER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC);

    @Override
    public String convertToDatabaseColumn(Instant attribute) {
        return attribute == null ? null : WRITER.format(attribute);
    }

    @Override
    public Instant convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return WRITER.parse(dbData, Instant::from);
        } catch (DateTimeParseException ex) {
            return Instant.parse(dbData);
        }
    }
}

@Repository
@Transactional(readOnly = true)
class JpaInventoryRepository implements InventoryRepository {

    private static final Map<String, String> ORDER_MAP = Map.of(
            "Quantity", "quantity",
            "DateOrder", "dateOrder",
            "CreatedAt", "createdAt",
            "UpdatedAt", "updatedAt",
            "DeletedAt", "deletedAt");

    private final InventorySpringDataRepository jpa;
    private final ProductSnapshotSpringDataRepository products;

    JpaInventoryRepository(InventorySpringDataRepository jpa, ProductSnapshotSpringDataRepository products) {
        this.jpa = jpa;
        this.products = products;
    }

    @Override
    @Transactional
    public Inventory save(Inventory inventory) {
        var product = products.getReferenceById(inventory.idProduct());
        var entity = jpa.findById(inventory.id()).map(existing -> {
            existing.setQuantity(inventory.quantity());
            existing.setDateOrder(DateOrderConverter.WRITER.format(inventory.dateOrder()));
            existing.setUpdatedAt(inventory.updatedAt());
            existing.setDeletedAt(inventory.deletedAt());
            existing.setDeleted(inventory.isDeleted());
            return existing;
        }).orElseGet(() -> new InventoryJpaEntity(inventory.id(), inventory.quantity(),
                DateOrderConverter.WRITER.format(inventory.dateOrder()), inventory.idProduct(), product,
                inventory.createdAt(), inventory.updatedAt(), inventory.deletedAt(), inventory.isDeleted()));

        jpa.save(entity);
        return inventory;
    }

    @Override
    public Optional<Inventory> findById(UUID id) {
        return jpa.findById(id).map(InventoryMapper::toDomain);
    }

    @Override
    public Optional<InventoryViewData> findDetailedById(UUID id) {
        return jpa.findById(id).map(InventoryMapper::toViewData);
    }

    @Override
    public SearchResult<InventoryViewData> search(InventoryCriteria c) {
        Pageable pageable = SpecificationSearch.pageable(c.order(), ORDER_MAP, c.page(), c.size());

        Specification<InventoryJpaEntity> spec = SpecificationSearch.withFilters((cb, root, predicates) -> {
            SpecificationSearch.equalIfNotNull(cb, root, predicates, quantityOrNull(c.quantity()), "quantity");
            SpecificationSearch.equalIfNotBlank(cb, root, predicates, c.dateOrder(), "dateOrder");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.idProduct(), "product");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.id(), "id");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.createdAt(), "createdAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.updatedAt(), "updatedAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.deletedAt(), "deletedAt");
        });

        var page = jpa.findAll(spec, pageable);
        List<InventoryViewData> data = page.getContent().stream().map(InventoryMapper::toViewData).toList();
        return new SearchResult<>(data, PagedResult.create(c.page(), c.size(), (int) page.getTotalElements()));
    }

    private Integer quantityOrNull(Integer quantity) {
        return quantity == null || quantity == 0 ? null : quantity;
    }

    @Override
    public SearchResult<InventoryViewData> searchGridify(io.github.mzet97.eestoque.shared.application.GridifyCriteria c) {
        return SpecificationSearch.gridifyPage(jpa, EntityFieldMaps.inventory(), c, items ->
                items.stream().map(InventoryMapper::toViewData).toList());
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}

@Repository
@Transactional(readOnly = true)
class JpaProductReader implements ProductReader {

    private final ProductSnapshotSpringDataRepository jpa;

    JpaProductReader(ProductSnapshotSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existsById(UUID id) {
        return jpa.existsById(id);
    }
}

final class InventoryMapper {

    private InventoryMapper() {
    }

    static Inventory toDomain(InventoryJpaEntity e) {
        var dateOrder = new DateOrderConverter().convertToEntityAttribute(e.getDateOrder());
        return Inventory.rehydrate(e.getId(), e.getQuantity(), dateOrder, e.getIdProduct(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt(), e.isDeleted());
    }

    static InventoryViewData toViewData(InventoryJpaEntity e) {
        ProductSnapshot product = null;
        var p = e.getProduct();
        if (p != null) {
            ProductSnapshot.CategorySnapshot category = null;
            if (p.getCategory() != null) {
                var c = p.getCategory();
                category = new ProductSnapshot.CategorySnapshot(c.getId(), c.getName(), c.getDescription(),
                        c.getShortDescription(), c.getCreatedAt(), c.getUpdatedAt(), c.getDeletedAt());
            }
            ProductSnapshot.CompanySnapshot company = null;
            if (p.getCompany() != null) {
                var co = p.getCompany();
                var address = new ProductSnapshot.CompanySnapshot.AddressSnapshot(co.getAddressStreet(),
                        co.getAddressNumber(), co.getAddressComplement(), co.getAddressNeighborhood(),
                        co.getAddressDistrict(), co.getAddressCity(), co.getAddressCountry(), co.getAddressZipCode(),
                        co.getAddressLatitude(), co.getAddressLongitude());
                company = new ProductSnapshot.CompanySnapshot(co.getId(), co.getName(), co.getDocId(), co.getEmail(),
                        co.getDescription(), co.getPhoneNumber(), address, co.getCreatedAt(), co.getUpdatedAt(),
                        co.getDeletedAt());
            }
            product = new ProductSnapshot(p.getId(), p.getName(), p.getDescription(), p.getShortDescription(),
                    p.getPrice(), p.getWeight(), p.getHeight(), p.getLength(), p.getImage(), p.getIdCategory(),
                    category, p.getIdCompany(), company, p.getCreatedAt(), p.getUpdatedAt(), p.getDeletedAt());
        }
        return new InventoryViewData(toDomain(e), product);
    }
}
