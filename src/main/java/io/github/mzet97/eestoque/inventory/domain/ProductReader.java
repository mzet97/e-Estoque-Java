package io.github.mzet97.eestoque.inventory.domain;

import java.util.UUID;

/** Verificação de existência de Product (referencial do Inventory). */
public interface ProductReader {

    boolean existsById(UUID id);
}
