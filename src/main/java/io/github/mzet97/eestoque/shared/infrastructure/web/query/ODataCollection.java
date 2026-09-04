package io.github.mzet97.eestoque.shared.infrastructure.web.query;

import java.util.LinkedHashMap;
import java.util.Map;

/** Envelope OData v4: {"value": [...], "@odata.count"?: n}. */
public final class ODataCollection {

    private ODataCollection() {
    }

    public static <T> Map<String, Object> of(java.util.List<T> items, boolean countRequested, int totalCount) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (countRequested) {
            body.put("@odata.count", totalCount);
        }
        body.put("value", items);
        return body;
    }
}
