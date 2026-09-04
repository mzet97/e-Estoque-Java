package io.github.mzet97.eestoque.product.domain;

import java.util.Optional;
import java.util.UUID;

/** Verificação de existência de Company (referencial do Product). */
public interface CompanyReader {

    boolean existsById(UUID id);
}
