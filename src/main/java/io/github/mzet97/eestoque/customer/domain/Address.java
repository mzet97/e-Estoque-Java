package io.github.mzet97.eestoque.customer.domain;

/**
 * Value object de endereço do cliente (contrato .NET CustomerAddress).
 * Todos os campos são strings obrigatórias (3–80) conforme validação original.
 */
public record Address(
        String street,
        String number,
        String complement,
        String neighborhood,
        String district,
        String city,
        String country,
        String zipCode,
        String latitude,
        String longitude) {
}
