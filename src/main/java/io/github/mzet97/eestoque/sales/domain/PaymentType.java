package io.github.mzet97.eestoque.sales.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import io.github.mzet97.eestoque.shared.domain.CodeEnum;

/**
 * Forma de pagamento (ints do contrato .NET): Pix=1, Deposit=2,
 * CreditCard=3, DebitCard=4.
 */
public enum PaymentType implements CodeEnum {
    PIX(1),
    DEPOSIT(2),
    CREDIT_CARD(3),
    DEBIT_CARD(4);

    private final int code;

    PaymentType(int code) {
        this.code = code;
    }

    @JsonValue
    public int toInt() {
        return code;
    }

    /** Paridade com PaymentTypeHelper.FromString (default Pix). */
    public static PaymentType fromStringOrDefault(String value, PaymentType fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        for (PaymentType type : values()) {
            var normalized = type.name().replace("_", "");
            if (normalized.equalsIgnoreCase(value) || String.valueOf(type.code).equals(value)) {
                return type;
            }
        }
        return fallback;
    }
}
