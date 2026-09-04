package io.github.mzet97.eestoque.shared.domain.validation;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * Verificações de invariantes com as mensagens exatas do sistema .NET
 * (FluentValidation). Acumulam erros na lista informada, na mesma ordem
 * das regras originais.
 *
 * Semânticas preservadas do FluentValidation:
 * - "NotEmpty" em números: falha quando o valor é o default (zero).
 * - "Length": não falha para null (o NotEmpty da regra anterior cobre).
 */
public final class Checks {

    private static final Instant MIN_DATE = LocalDate.of(1900, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
    private static final Instant MAX_DATE = LocalDate.of(3000, 12, 31).atStartOfDay().toInstant(ZoneOffset.UTC);

    private Checks() {
    }

    /** EntityValidation: id e datas de auditoria. */
    public static void audit(UUID id, Instant createdAt, Instant updatedAt, Instant deletedAt, List<String> errors) {
        if (id == null) {
            errors.add("The Id cannot be empty.");
        }
        if (createdAt == null) {
            errors.add("The creation date must be provided.");
        } else if (createdAt.isBefore(MIN_DATE) || createdAt.isAfter(MAX_DATE)) {
            errors.add("The creation date must be between 1900 and 3000.");
        }
        auditDate(updatedAt, "UpdatedAt", errors);
        auditDate(deletedAt, "DeletedAt", errors);
    }

    private static void auditDate(Instant date, String property, List<String> errors) {
        if (date != null && (date.isBefore(MIN_DATE) || date.isAfter(MAX_DATE))) {
            errors.add(property + " must be a valid date between 1900 and 3000 or null.");
        }
    }

    /** "The {Property} needs to be provided" — string (NotEmpty). */
    public static void provided(List<String> errors, String value, String property) {
        if (value == null || value.isBlank()) {
            errors.add("The %s needs to be provided".formatted(property));
        }
    }

    /** "{Property} is required" — string (NotEmpty, estilo Company/Customer). */
    public static void required(List<String> errors, String value, String property) {
        if (value == null || value.isBlank()) {
            errors.add("%s is required".formatted(property));
        }
    }

    /** "{Property} is required" — objeto/VO não nulo. */
    public static void required(List<String> errors, Object value, String property) {
        if (value == null) {
            errors.add("%s is required".formatted(property));
        }
    }

    /** "The {Property} needs to be provided" — número não pode ser default/zero. */
    public static void provided(List<String> errors, Integer value, String property) {
        if (value == null || value == 0) {
            errors.add("The %s needs to be provided".formatted(property));
        }
    }

    /** "The {Property} needs to be provided" — decimal não pode ser default/zero. */
    public static void provided(List<String> errors, BigDecimal value, String property) {
        if (value == null || value.signum() == 0) {
            errors.add("The %s needs to be provided".formatted(property));
        }
    }

    /** "The {Property} needs to be provided" — enum/tipo identificador. */
    public static void provided(List<String> errors, Object value, String property) {
        if (value == null) {
            errors.add("The %s needs to be provided".formatted(property));
        }
    }

    /** "The {Property} need to have between {min} and {max} characters". */
    public static void length(List<String> errors, String value, String property, int min, int max) {
        if (value != null && (value.length() < min || value.length() > max)) {
            errors.add("The %s need to have between %d and %d characters".formatted(property, min, max));
        }
    }

    /** "The {Property} needs to be greater than 0" — inteiro. */
    public static void positive(List<String> errors, Integer value, String property) {
        if (value != null && value <= 0) {
            errors.add("The %s needs to be greater than 0".formatted(property));
        }
    }

    /** "The {Property} needs to be greater 0" — decimal (mensagem do Product). */
    public static void positiveDecimal(List<String> errors, BigDecimal value, String property) {
        if (value != null && value.signum() <= 0) {
            errors.add("The %s needs to be greater 0".formatted(property));
        }
    }

    /** "The {Property} need to be between {from} and {to}" — faixa inclusiva. */
    public static void inclusiveRange(List<String> errors, BigDecimal value, String property, int from, int to) {
        if (value != null && (value.compareTo(BigDecimal.valueOf(from)) < 0 || value.compareTo(BigDecimal.valueOf(to)) > 0)) {
            errors.add("The %s need to be between %d and %d".formatted(property, from, to));
        }
    }

    /** "The {Property} cannot be empty." — UUID obrigatório. */
    public static void notEmptyGuid(List<String> errors, UUID value, String property) {
        if (value == null) {
            errors.add("The %s cannot be empty.".formatted(property));
        }
    }

    /** "The {Property} needs to be provided" — Instant obrigatório. */
    public static void provided(List<String> errors, Instant value, String property) {
        if (value == null) {
            errors.add("The %s needs to be provided".formatted(property));
        }
    }

    /** Acumula a mensagem quando a condição indicar falha. */
    public static void when(boolean invalid, String message, List<String> errors) {
        if (invalid) {
            errors.add(message);
        }
    }
}
