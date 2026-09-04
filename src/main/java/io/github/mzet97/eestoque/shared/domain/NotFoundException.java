package io.github.mzet97.eestoque.shared.domain;

/** Recurso não encontrado — mapeado para HTTP 404 com {"error": mensagem}. */
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Not found");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
