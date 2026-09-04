package io.github.mzet97.eestoque.shared.application;

/**
 * Contrato comum das buscas paginadas (equivalente ao BaseSearch .NET):
 * pageIndex default 1, pageSize default 10, com limite máximo aplicado
 * pela camada web.
 */
public interface PagedSearch {

    int MAX_PAGE_SIZE = 1000;

    String order();

    Integer pageIndex();

    Integer pageSize();

    default int pageOrDefault() {
        return pageIndex() == null || pageIndex() < 1 ? 1 : pageIndex();
    }

    default int sizeOrDefault() {
        int size = pageSize() == null || pageSize() < 1 ? 10 : pageSize();
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
