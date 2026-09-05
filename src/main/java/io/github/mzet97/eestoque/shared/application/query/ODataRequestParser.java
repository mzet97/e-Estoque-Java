package io.github.mzet97.eestoque.shared.application.query;

import io.github.mzet97.eestoque.shared.application.GridifyCriteria;

/** Vínculo parâmetros OData → GridifyCriteria (subset ADR-011). */
public final class ODataRequestParser {

    private ODataRequestParser() {
    }

    public static GridifyCriteria toCriteria(String filter, String orderBy, Integer top, Integer skip) {
        int size = top == null || top < 1 ? 1000 : Math.min(top, 1000);
        int page = top == null || top < 1 ? 1 : (skip == null ? 1 : skip / top + 1);
        return new GridifyCriteria(ODataFilterParser.parse(filter), orderBy, page, size);
    }
}
