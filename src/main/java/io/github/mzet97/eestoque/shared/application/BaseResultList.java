package io.github.mzet97.eestoque.shared.application;

import java.util.List;

/**
 * Envelope de resposta de listas: {"data": [...], "pagedResult": {...},
 * "success": true, "message": ""}.
 */
public record BaseResultList<T>(List<T> data, PagedResult pagedResult, boolean success, String message) {

    public static <T> BaseResultList<T> of(List<T> data, PagedResult pagedResult) {
        return new BaseResultList<>(data, pagedResult, true, "");
    }
}
