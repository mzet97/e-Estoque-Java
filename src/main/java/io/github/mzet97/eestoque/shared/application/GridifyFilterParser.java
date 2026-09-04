package io.github.mzet97.eestoque.shared.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.mzet97.eestoque.shared.application.FilterNode.Condition;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

/**
 * Parser do subconjunto Gridify usado pelo sistema original (ADR-011):
 * condições por & (AND) e | (OR); operadores =, !=, >, <, >=, <=, ^=,
 * $=, *= e a forma =*valor* (contains).
 */
public final class GridifyFilterParser {

    private GridifyFilterParser() {
    }

    public static FilterNode parse(String filter) {
        if (filter == null || filter.isBlank()) {
            return null;
        }
        List<FilterNode> ors = new ArrayList<>();
        for (String orPart : filter.split("\\|")) {
            List<FilterNode> ands = new ArrayList<>();
            for (String andPart : orPart.split("&")) {
                ands.add(parseCondition(andPart.trim()));
            }
            ors.add(ands.size() == 1 ? ands.getFirst() : new FilterNode.And(ands));
        }
        return ors.size() == 1 ? ors.getFirst() : new FilterNode.Or(ors);
    }

    private static Condition parseCondition(String condition) {
        for (var op : List.of("==", "!=", ">=", "<=", "^=", "$=", "*=", "=", ">", "<")) {
            int idx = condition.indexOf(op);
            if (idx <= 0) {
                continue;
            }
            var field = condition.substring(0, idx).trim();
            var rawValue = condition.substring(idx + op.length()).trim();

            var operator = switch (op) {
                case "==", "=" -> FilterNode.Operator.EQ;
                case "!=" -> FilterNode.Operator.NE;
                case ">=" -> FilterNode.Operator.GE;
                case "<=" -> FilterNode.Operator.LE;
                case "^=" -> FilterNode.Operator.STARTS_WITH;
                case "$=" -> FilterNode.Operator.ENDS_WITH;
                case ">" -> FilterNode.Operator.GT;
                case "<" -> FilterNode.Operator.LT;
                case "*=" -> FilterNode.Operator.CONTAINS;
                default -> FilterNode.Operator.EQ;
            };

            // Sintaxe =*texto* (contains) e =*texto (endswith no estilo Gridify antigo)
            if (operator == FilterNode.Operator.EQ && rawValue.startsWith("*")) {
                operator = rawValue.endsWith("*") ? FilterNode.Operator.CONTAINS : FilterNode.Operator.CONTAINS;
                rawValue = rawValue.substring(1, rawValue.endsWith("*") ? rawValue.length() - 1 : rawValue.length());
            } else if (operator == FilterNode.Operator.CONTAINS && rawValue.startsWith("*") && rawValue.endsWith("*")) {
                rawValue = rawValue.substring(1, rawValue.length() - 1);
            }
            if (field.isBlank()) {
                throw new ValidationException("Invalid gridify filter: " + condition);
            }
            return new Condition(field, operator, unquote(rawValue));
        }
        throw new ValidationException("Invalid gridify filter: " + condition);
    }

    private static String unquote(String value) {
        if (value != null && value.length() >= 2 && value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    /** "name asc, createdAt desc" → pares atributo/direção. */
    public static List<OrderTerm> parseOrderBy(String orderBy) {
        if (orderBy == null || orderBy.isBlank()) {
            return List.of();
        }
        return Arrays.stream(orderBy.split(","))
                .map(String::trim)
                .filter(term -> !term.isEmpty())
                .map(term -> {
                    var parts = term.split("\\s+");
                    var desc = parts.length > 1 && parts[1].equalsIgnoreCase("desc");
                    return new OrderTerm(parts[0], desc);
                })
                .toList();
    }

    public record OrderTerm(String field, boolean descending) {
    }
}
