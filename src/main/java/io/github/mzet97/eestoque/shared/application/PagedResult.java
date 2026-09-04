package io.github.mzet97.eestoque.shared.application;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Paginação equivalente ao PagedResult do sistema original, incluindo os
 * campos calculados firstRowOnPage/lastRowOnPage do contrato JSON.
 */
public record PagedResult(int currentPage, int pageCount, int pageSize, int rowCount) {

    public static PagedResult create(int currentPage, int pageSize, int rowCount) {
        int safePageSize = Math.max(pageSize, 1);
        int pageCount = (int) Math.ceil((double) rowCount / safePageSize);
        return new PagedResult(currentPage, pageCount, safePageSize, rowCount);
    }

    public int skip() {
        return (currentPage - 1) * pageSize;
    }

    @JsonProperty("firstRowOnPage")
    public int firstRowOnPage() {
        return (currentPage - 1) * pageSize + 1;
    }

    @JsonProperty("lastRowOnPage")
    public int lastRowOnPage() {
        return Math.min(currentPage * pageSize, rowCount);
    }
}
