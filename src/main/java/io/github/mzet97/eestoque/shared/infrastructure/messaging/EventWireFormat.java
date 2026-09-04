package io.github.mzet97.eestoque.shared.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.mzet97.eestoque.shared.domain.DomainEvent;

/**
 * Wire-format do broker, idêntico ao RabbitMqClient .NET: JSON camelCase,
 * nulls ignorados (WhenWritingNull), routing key = nome da classe em
 * dash-case.
 */
public final class EventWireFormat {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private EventWireFormat() {
    }

    public static String serialize(Object event) {
        try {
            return MAPPER.writeValueAsString(event);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize event", ex);
        }
    }

    public static String routingKey(DomainEvent event) {
        return toDashCase(event.getClass().getSimpleName());
    }

    public static String toDashCase(String name) {
        if (name == null || name.length() < 2) {
            return name;
        }
        var sb = new StringBuilder();
        sb.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('-').append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** NotificationError do .NET: {message, details}. */
    public record NotificationPayload(String message, String details) {
    }

    public static NotificationPayload notification(String message, String details) {
        return new NotificationPayload(message, details);
    }
}
