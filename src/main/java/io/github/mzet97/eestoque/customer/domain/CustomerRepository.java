package io.github.mzet97.eestoque.customer.domain;

import java.util.Optional;
import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.SearchResult;

/** Porta de persistência de clientes. Delete = hard delete (parity). */
public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    SearchResult<Customer> search(CustomerCriteria criteria);

    void deleteById(UUID id);
}
