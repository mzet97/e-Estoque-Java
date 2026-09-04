package io.github.mzet97.eestoque.company.domain;

/**
 * Value object de endereço (contrato .NET CompanyAddress/CustomerAddress).
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
