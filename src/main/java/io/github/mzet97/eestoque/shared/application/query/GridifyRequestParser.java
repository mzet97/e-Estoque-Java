package io.github.mzet97.eestoque.shared.application.query;

import io.github.mzet97.eestoque.shared.application.GridifyCriteria;
import io.github.mzet97.eestoque.shared.application.GridifyFilterParser;
import io.github.mzet97.eestoque.shared.domain.validation.Checks;

/** Vínculo request → GridifyCriteria (page default 1, size default 10, cap 1000). */
public final class GridifyRequestParser {

    private GridifyRequestParser() {
    }

    public static GridifyCriteria toCriteria(String filter, String orderBy, Integer page, Integer pageSize) {
        int p = page == null || page < 1 ? 1 : page;
        int size = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 1000);
        return new GridifyCriteria(GridifyFilterParser.parse(filter), orderBy, p, size);
    }
}
