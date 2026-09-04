package io.github.mzet97.eestoque.shared.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import io.github.mzet97.eestoque.shared.application.FilterNode;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

/**
 * Traduz {@link FilterNode} em {@link Specification} a partir de um mapa de
 * campos por entidade (whitelist). Usado pelos endpoints gridify e odata.
 */
public final class FilterSpecificationBuilder {

    public enum FieldType {
        STRING, NUMBER, INSTANT, BOOLEAN, UUID
    }

    public record FieldDef(String attribute, FieldType type) {
    }

    private FilterSpecificationBuilder() {
    }

    public static <T> Specification<T> toSpecification(FilterNode node, Map<String, FieldDef> fields) {
        return (root, query, cb) -> buildPredicate(node, root, cb, fields);
    }

    private static <T> Predicate buildPredicate(FilterNode node, Root<T> root, CriteriaBuilder cb,
                                                Map<String, FieldDef> fields) {
        if (node instanceof FilterNode.And and) {
            List<Predicate> predicates = and.children().stream()
                    .map(child -> buildPredicate(child, root, cb, fields))
                    .toList();
            return cb.and(predicates.toArray(Predicate[]::new));
        }
        if (node instanceof FilterNode.Or or) {
            List<Predicate> predicates = or.children().stream()
                    .map(child -> buildPredicate(child, root, cb, fields))
                    .toList();
            return cb.or(predicates.toArray(Predicate[]::new));
        }
        if (node instanceof FilterNode.Condition condition) {
            var def = fields.get(condition.field().toLowerCase());
            if (def == null) {
                throw new ValidationException("Unknown filter field: " + condition.field());
            }
            return predicateFor(condition, path(root, def.attribute()), cb);
        }
        throw new ValidationException("Unsupported filter node");
    }

    @SuppressWarnings("java:S1452")
    private static <T> Path<?> path(Root<T> root, String dottedAttribute) {
        Path<?> current = null;
        for (String part : dottedAttribute.split("\\.")) {
            current = current == null ? root.get(part) : current.get(part);
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    private static Predicate predicateFor(FilterNode.Condition condition, Path<?> path, CriteriaBuilder cb) {
        var raw = condition.value();
        try {
            var op = condition.operator();
            return switch (path.getJavaType().getSimpleName()) {
                case "String" -> stringPredicate(condition, (Path<String>) path, cb);
                case "BigDecimal" -> comparison(op, cb, path, new BigDecimal(raw));
                case "Integer" -> comparison(op, cb, path, Integer.valueOf(raw));
                case "Long" -> comparison(op, cb, path, Long.valueOf(raw));
                case "boolean", "Boolean" -> booleanPredicate(op, cb, path, Boolean.parseBoolean(raw));
                case "Instant" -> comparison(op, cb, path, Instant.parse(raw));
                case "UUID" -> uuidPredicate(op, cb, path, UUID.fromString(raw));
                default -> throw new ValidationException("Unsupported field type: " + path.getJavaType());
            };
        } catch (NumberFormatException | ClassCastException ex) {
            throw new ValidationException("Invalid filter value '%s' for %s".formatted(raw, condition.field()));
        }
    }

    private static Predicate stringPredicate(FilterNode.Condition condition, Path<String> path, CriteriaBuilder cb) {
        var value = condition.value();
        return switch (condition.operator()) {
            case EQ -> cb.equal(path, value);
            case NE -> cb.notEqual(path, value);
            case CONTAINS -> cb.like(cb.lower(path), "%" + value.toLowerCase() + "%");
            case STARTS_WITH -> cb.like(cb.lower(path), value.toLowerCase() + "%");
            case ENDS_WITH -> cb.like(cb.lower(path), "%" + value.toLowerCase());
            default -> throw new ValidationException("Operator %s not supported for strings".formatted(condition.operator()));
        };
    }

    private static Predicate booleanPredicate(FilterNode.Operator op, CriteriaBuilder cb, Path<?> path,
                                              Boolean value) {
        return switch (op) {
            case EQ -> cb.equal(path, value);
            case NE -> cb.notEqual(path, value);
            default -> throw new ValidationException("Operator not supported for booleans");
        };
    }

    private static Predicate uuidPredicate(FilterNode.Operator op, CriteriaBuilder cb, Path<?> path, UUID value) {
        return switch (op) {
            case EQ -> cb.equal(path, value);
            case NE -> cb.notEqual(path, value);
            default -> throw new ValidationException("Operator not supported for UUIDs");
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Comparable<? super T>> Predicate comparison(FilterNode.Operator op, CriteriaBuilder cb,
                                                                          Path<?> path, T value) {
        Class<T> type = (Class<T>) value.getClass();
        jakarta.persistence.criteria.Expression<T> expression = path.as(type);
        return switch (op) {
            case EQ -> cb.equal(path, value);
            case NE -> cb.notEqual(path, value);
            case GT -> cb.greaterThan(expression, value);
            case GE -> cb.greaterThanOrEqualTo(expression, value);
            case LT -> cb.lessThan(expression, value);
            case LE -> cb.lessThanOrEqualTo(expression, value);
            default -> throw new ValidationException("Operator not supported for ordered types");
        };
    }

    /** Ordenação a partir de termos whitelistados. */
    public static List<jakarta.persistence.criteria.Order> orders(
            List<io.github.mzet97.eestoque.shared.application.GridifyFilterParser.OrderTerm> terms,
            Map<String, FieldDef> fields, Root<?> root, CriteriaBuilder cb) {
        var orders = new ArrayList<jakarta.persistence.criteria.Order>();
        for (var term : terms) {
            var def = fields.get(term.field().toLowerCase());
            if (def == null) {
                throw new ValidationException("Unknown order field: " + term.field());
            }
            var path = path(root, def.attribute());
            orders.add(term.descending() ? cb.desc(path) : cb.asc(path));
        }
        if (orders.isEmpty()) {
            orders.add(cb.asc(root.get("id")));
        }
        return orders;
    }

    /** Helper de conversão segura de valor para tipos de campo. */
    public static Object typedValue(FieldDef def, String raw) {
        return switch (def.type()) {
            case NUMBER -> new BigDecimal(raw);
            case INSTANT -> Instant.parse(raw);
            case BOOLEAN -> Boolean.parseBoolean(raw);
            case UUID -> UUID.fromString(raw);
            default -> raw;
        };
    }
}
