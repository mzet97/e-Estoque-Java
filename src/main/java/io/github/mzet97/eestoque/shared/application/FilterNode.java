package io.github.mzet97.eestoque.shared.application;

import java.util.List;

/**
 * Árvore de filtros das buscas Gridify/OData (subset do contrato original,
 * ADR-011). Condições por campo; combinadores AND/OR.
 */
public sealed interface FilterNode {

    record And(List<FilterNode> children) implements FilterNode {
    }

    record Or(List<FilterNode> children) implements FilterNode {
    }

    record Condition(String field, Operator operator, String value) implements FilterNode {
    }

    enum Operator {
        EQ, NE, GT, GE, LT, LE, CONTAINS, STARTS_WITH, ENDS_WITH
    }
}
