package io.github.mzet97.eestoque.sales.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.mzet97.eestoque.sales.domain.PaymentType;
import io.github.mzet97.eestoque.sales.domain.Sale;
import io.github.mzet97.eestoque.sales.domain.SaleCriteria;
import io.github.mzet97.eestoque.sales.domain.SaleProduct;
import io.github.mzet97.eestoque.sales.domain.SaleReferenceReader;
import io.github.mzet97.eestoque.sales.domain.SaleRepository;
import io.github.mzet97.eestoque.sales.domain.SaleType;
import io.github.mzet97.eestoque.sales.domain.SaleViewData;
import io.github.mzet97.eestoque.shared.application.PagedResult;
import io.github.mzet97.eestoque.shared.application.SearchResult;
import io.github.mzet97.eestoque.shared.infrastructure.persistence.SpecificationSearch;

interface SaleSpringDataRepository
        extends JpaRepository<SaleJpaEntity, UUID>, JpaSpecificationExecutor<SaleJpaEntity> {
}

interface CustomerSnapshotSpringDataRepository extends JpaRepository<CustomerSnapshotJpaEntity, UUID> {
}

interface SaleProductSnapshotSpringDataRepository extends JpaRepository<ProductSnapshotJpaEntity, UUID> {
}

@Repository
@Transactional(readOnly = true)
class JpaSaleRepository implements SaleRepository {

    private static final Map<String, String> ORDER_MAP = Map.ofEntries(
            Map.entry("Quantity", "quantity"),
            Map.entry("TotalPrice", "totalPrice"),
            Map.entry("TotalTax", "totalTax"),
            Map.entry("SaleType", "saleType"),
            Map.entry("PaymentType", "paymentType"),
            Map.entry("DeliveryDate", "deliveryDate"),
            Map.entry("SaleDate", "saleDate"),
            Map.entry("PaymentDate", "paymentDate"),
            Map.entry("IdCustomer", "idCustomer"),
            Map.entry("CreatedAt", "createdAt"),
            Map.entry("UpdatedAt", "updatedAt"),
            Map.entry("DeletedAt", "deletedAt"));

    private final SaleSpringDataRepository jpa;
    private final CustomerSnapshotSpringDataRepository customers;
    private final SaleProductSnapshotSpringDataRepository products;

    JpaSaleRepository(SaleSpringDataRepository jpa, CustomerSnapshotSpringDataRepository customers,
                      SaleProductSnapshotSpringDataRepository products) {
        this.jpa = jpa;
        this.customers = customers;
        this.products = products;
    }

    @Override
    @Transactional
    public Sale save(Sale sale) {
        var customer = customers.getReferenceById(sale.idCustomer());

        var entity = jpa.findById(sale.id()).orElseGet(() -> new SaleJpaEntity(sale.id(), sale.createdAt()));
        entity.setQuantity(sale.quantity());
        entity.setTotalPrice(sale.totalPrice());
        entity.setTotalTax(sale.totalTax());
        entity.setSaleType(sale.saleType() == null ? 0 : sale.saleType().toInt());
        entity.setPaymentType(sale.paymentType() == null ? 0 : sale.paymentType().toInt());
        entity.setDeliveryDate(sale.deliveryDate());
        entity.setSaleDate(sale.saleDate());
        entity.setPaymentDate(sale.paymentDate());
        entity.setIdCustomer(sale.idCustomer());
        entity.setUpdatedAt(sale.updatedAt());
        entity.setDeletedAt(sale.deletedAt());
        entity.setDeleted(sale.isDeleted());

        entity.getSaleProducts().clear();
        for (SaleProduct item : sale.saleProducts()) {
            var product = products.getReferenceById(item.idProduct());
            var itemEntity = new SaleProductJpaEntity(item.id(), item.createdAt());
            itemEntity.setQuantity(item.quantity());
            itemEntity.setProduct(product);
            itemEntity.setSale(entity);
            itemEntity.setDeleted(false);
            entity.getSaleProducts().add(itemEntity);
        }

        jpa.save(entity);
        return sale;
    }

    @Override
    public Optional<Sale> findById(UUID id) {
        return jpa.findById(id).map(SaleMapper::toDomain);
    }

    @Override
    public Optional<SaleViewData> findDetailedById(UUID id) {
        return jpa.findById(id).map(entity -> {
            var customer = customers.findById(entity.getIdCustomer()).map(SaleMapper::toCustomerSnapshot)
                    .orElse(null);
            var productSnapshots = entity.getSaleProducts().stream()
                    .map(item -> SaleMapper.toProductSnapshot(item.getProduct()))
                    .toList();
            return new SaleViewData(SaleMapper.toDomain(entity), customer, productSnapshots);
        });
    }

    @Override
    public SearchResult<SaleViewData> search(SaleCriteria c) {
        Pageable pageable = SpecificationSearch.pageable(c.order(), ORDER_MAP, c.page(), c.size());

        Specification<SaleJpaEntity> spec = SpecificationSearch.withFilters((cb, root, predicates) -> {
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.quantityOrDefault(), "quantity");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.totalPriceOrDefault(), "totalPrice");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.totalTaxOrDefault(), "totalTax");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.saleTypeCode(), "saleType");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.paymentTypeCode(), "paymentType");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.deliveryDate(), "deliveryDate");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.saleDate(), "saleDate");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.paymentDate(), "paymentDate");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.idCustomer(), "idCustomer");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.id(), "id");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.createdAt(), "createdAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.updatedAt(), "updatedAt");
            SpecificationSearch.equalIfNotNull(cb, root, predicates, c.deletedAt(), "deletedAt");
            if (c.idProduct() != null) {
                var subquery = root.get("saleProducts");
                predicates.add(cb.equal(subquery.get("idProduct"), c.idProduct()));
            }
        });

        var page = jpa.findAll(spec, pageable);
        List<SaleViewData> data = page.getContent().stream().map(entity -> {
            var customer = customers.findById(entity.getIdCustomer()).map(SaleMapper::toCustomerSnapshot)
                    .orElse(null);
            var productSnapshots = entity.getSaleProducts().stream()
                    .map(item -> SaleMapper.toProductSnapshot(item.getProduct()))
                    .toList();
            return new SaleViewData(SaleMapper.toDomain(entity), customer, productSnapshots);
        }).toList();
        return new SearchResult<>(data, PagedResult.create(c.page(), c.size(), (int) page.getTotalElements()));
    }

    @Override
    @Transactional
    public void disable(UUID id, java.time.Instant deletedAt) {
        jpa.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            entity.setDeletedAt(deletedAt);
        });
    }
}

@Repository
@Transactional(readOnly = true)
class JpaSaleReferenceReader implements SaleReferenceReader {

    private final CustomerSnapshotSpringDataRepository customers;
    private final SaleProductSnapshotSpringDataRepository products;

    JpaSaleReferenceReader(CustomerSnapshotSpringDataRepository customers,
                           SaleProductSnapshotSpringDataRepository products) {
        this.customers = customers;
        this.products = products;
    }

    @Override
    public boolean customerExists(UUID id) {
        return customers.existsById(id);
    }

    @Override
    public boolean productExists(UUID id) {
        return products.existsById(id);
    }
}

final class SaleMapper {

    private SaleMapper() {
    }

    static Sale toDomain(SaleJpaEntity e) {
        var items = e.getSaleProducts().stream()
                .map(i -> SaleProduct.rehydrate(i.getId(), i.getQuantity(), i.getIdProduct(), e.getId(),
                        i.getCreatedAt(), i.getUpdatedAt(), i.getDeletedAt(), i.isDeleted()))
                .toList();
        return Sale.rehydrate(e.getId(), e.getQuantity(), e.getTotalPrice(), e.getTotalTax(),
                fromCode(e.getSaleType(), SaleType.values()),
                fromCode(e.getPaymentType(), PaymentType.values()),
                e.getDeliveryDate(), e.getSaleDate(), e.getPaymentDate(), e.getIdCustomer(),
                new ArrayList<>(items), e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt(), e.isDeleted());
    }

    private static <E extends java.lang.Enum<E> & io.github.mzet97.eestoque.shared.domain.CodeEnum> E fromCode(
            Integer code, E[] values) {
        if (code == null || code <= 0) {
            return null;
        }
        for (E value : values) {
            if (((io.github.mzet97.eestoque.shared.domain.CodeEnum) value).toInt() == code) {
                return value;
            }
        }
        return null;
    }

    static SaleViewData.CustomerSnapshot toCustomerSnapshot(CustomerSnapshotJpaEntity e) {
        var address = new SaleViewData.CustomerSnapshot.AddressSnapshot(e.getAddressStreet(), e.getAddressNumber(),
                e.getAddressComplement(), e.getAddressNeighborhood(), e.getAddressDistrict(), e.getAddressCity(),
                e.getAddressCountry(), e.getAddressZipCode(), e.getAddressLatitude(), e.getAddressLongitude());
        return new SaleViewData.CustomerSnapshot(e.getId(), e.getName(), e.getDocId(), e.getEmail(),
                e.getDescription(), e.getPhoneNumber(), address, e.getCreatedAt(), e.getUpdatedAt(),
                e.getDeletedAt());
    }

    static SaleViewData.ProductSnapshot toProductSnapshot(ProductSnapshotJpaEntity e) {
        SaleViewData.ProductSnapshot.CategorySnapshot category = null;
        if (e.getCategory() != null) {
            var c = e.getCategory();
            category = new SaleViewData.ProductSnapshot.CategorySnapshot(c.getId(), c.getName(), c.getDescription(),
                    c.getShortDescription(), c.getCreatedAt(), c.getUpdatedAt(), c.getDeletedAt());
        }
        SaleViewData.ProductSnapshot.CompanySnapshot company = null;
        if (e.getCompany() != null) {
            var c = e.getCompany();
            var address = new SaleViewData.ProductSnapshot.CompanySnapshot.AddressSnapshot(c.getAddressStreet(),
                    c.getAddressNumber(), c.getAddressComplement(), c.getAddressNeighborhood(),
                    c.getAddressDistrict(), c.getAddressCity(), c.getAddressCountry(), c.getAddressZipCode(),
                    c.getAddressLatitude(), c.getAddressLongitude());
            company = new SaleViewData.ProductSnapshot.CompanySnapshot(c.getId(), c.getName(), c.getDocId(),
                    c.getEmail(), c.getDescription(), c.getPhoneNumber(), address, c.getCreatedAt(),
                    c.getUpdatedAt(), c.getDeletedAt());
        }
        return new SaleViewData.ProductSnapshot(e.getId(), e.getName(), e.getDescription(), e.getShortDescription(),
                e.getPrice(), e.getWeight(), e.getHeight(), e.getLength(), e.getImage(), e.getIdCategory(),
                category, e.getIdCompany(), company, e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt());
    }
}
