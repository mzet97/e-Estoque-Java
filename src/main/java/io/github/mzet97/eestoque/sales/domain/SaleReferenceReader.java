package io.github.mzet97.eestoque.sales.domain;

import java.util.UUID;

/** Verificações referenciais da venda (Customer e Products existem). */
public interface SaleReferenceReader {

    boolean customerExists(UUID id);

    boolean productExists(UUID id);
}
