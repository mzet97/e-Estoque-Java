package io.github.mzet97.eestoque.shared.application;

/**
 * Envelope de resposta de item único: {"data": T, "success": true, "message": ""}.
 */
public record BaseResult<T>(T data, boolean success, String message) {

    public static <T> BaseResult<T> of(T data) {
        return new BaseResult<>(data, true, "");
    }
}
