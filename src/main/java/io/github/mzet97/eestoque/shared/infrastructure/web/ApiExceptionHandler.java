package io.github.mzet97.eestoque.shared.infrastructure.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.mzet97.eestoque.shared.domain.ForbiddenAccessException;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

/**
 * Mapeamento de exceções idêntico ao ExceptionMiddleware do sistema .NET:
 * corpo {"error": "<mensagem>"} nos mesmos status codes.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    public record ErrorResponse(String error) {
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(NotFoundException ex) {
        return respond(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Rota inexistente (ex.: id vazio) — 404 como no .NET, não 500. */
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> noResource(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        return respond(HttpStatus.NOT_FOUND, "Not found");
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> validation(ValidationException ex) {
        return respond(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<ErrorResponse> forbidden(ForbiddenAccessException ex) {
        return respond(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * Deixa o Spring Security tratar negações de autorização do filter chain
     * (403 com o contrato do framework, como no .NET).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public void rethrowSecurity(AccessDeniedException ex) throws AccessDeniedException {
        throw ex;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> unexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private static ResponseEntity<ErrorResponse> respond(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(message));
    }
}
