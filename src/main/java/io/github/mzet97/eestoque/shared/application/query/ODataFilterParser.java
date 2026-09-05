package io.github.mzet97.eestoque.shared.application.query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.mzet97.eestoque.shared.application.FilterNode;
import io.github.mzet97.eestoque.shared.domain.ValidationException;

/**
 * Parser do subconjunto OData v4 usado pelo frontend (ADR-011):
 * eq/ne/gt/ge/lt/le, contains(), startswith(), endswith(), and/or.
 */
public final class ODataFilterParser {

    private static final Pattern FUNCTION = Pattern
            .compile("(contains|startswith|endswith)\\(\\s*([A-Za-z_/]+)\\s*,\\s*'([^']*)'\\s*\\)");

    private ODataFilterParser() {
    }

    public static FilterNode parse(String filter) {
        if (filter == null || filter.isBlank()) {
            return null;
        }
        List<FilterNode> ors = new ArrayList<>();
        for (String orPart : filter.split("(?i)\\bor\\b")) {
            List<FilterNode> ands = new ArrayList<>();
            for (String andPart : orPart.split("(?i)\\band\\b")) {
                ands.add(parseCondition(andPart.trim()));
            }
            ors.add(ands.size() == 1 ? ands.getFirst() : new FilterNode.And(ands));
        }
        return ors.size() == 1 ? ors.getFirst() : new FilterNode.Or(ors);
    }

    private static FilterNode parseCondition(String condition) {
        var function = FUNCTION.matcher(condition);
        if (function.matches()) {
            var operator = switch (function.group(1)) {
                case "contains" -> FilterNode.Operator.CONTAINS;
                case "startswith" -> FilterNode.Operator.STARTS_WITH;
                default -> FilterNode.Operator.ENDS_WITH;
            };
            return new FilterNode.Condition(normalizeField(function.group(2)), operator, function.group(3));
        }
        for (var op : List.of(" ne ", " ge ", " le ", " eq ", " gt ", " lt ")) {
            int idx = condition.toLowerCase().indexOf(op);
            if (idx > 0) {
                var field = condition.substring(0, idx).trim();
                var raw = condition.substring(idx + op.length()).trim();
                var value = raw.startsWith("'") && raw.endsWith("'") ? raw.substring(1, raw.length() - 1) : raw;
                var operator = switch (op.trim()) {
                    case "ne" -> FilterNode.Operator.NE;
                    case "ge" -> FilterNode.Operator.GE;
                    case "le" -> FilterNode.Operator.LE;
                    case "gt" -> FilterNode.Operator.GT;
                    case "lt" -> FilterNode.Operator.LT;
                    default -> FilterNode.Operator.EQ;
                };
                return new FilterNode.Condition(normalizeField(field), operator, value);
            }
        }
        throw new ValidationException("Unsupported $filter expression: " + condition);
    }

    private static String normalizeField(String field) {
        var path = field;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        int slash = path.lastIndexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }
}
