package io.github.mzet97.eestoque.shared.domain;

/**
 * Falha de regra/invariante — mapeada para HTTP 400 com {"error": mensagem}.
 * A mensagem carrega os erros unidos por vírgula (contrato .NET).
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
