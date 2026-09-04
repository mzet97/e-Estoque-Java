package io.github.mzet97.eestoque.sales.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.github.mzet97.eestoque.shared.domain.CodeEnum;

/**
 * Tipo da venda (ints do contrato .NET). Serializa como número; valores
 * inválidos chegam como null (equivalente ao binding default 0 do .NET).
 */
public enum SaleType implements CodeEnum {
    UNITARY(1),
    RECURRENT(2);

    private final int code;

    SaleType(int code) {
        this.code = code;
    }

    @JsonValue
    public int toInt() {
        return code;
    }

    /** Paridade com SaleTypeHelper.FromString (default Unitary). */
    public static SaleType fromStringOrDefault(String value, SaleType fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        for (SaleType type : values()) {
            if (type.name().equalsIgnoreCase(value) || String.valueOf(type.code).equals(value)) {
                return type;
            }
        }
        return fallback;
    }
}
