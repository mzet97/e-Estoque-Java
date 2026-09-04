package io.github.mzet97.eestoque.shared.domain;

/** Operação não permitida — mapeada para HTTP 403 com {"error": mensagem}. */
public class ForbiddenAccessException extends RuntimeException {

    public ForbiddenAccessException(String message) {
        super(message);
    }
}
