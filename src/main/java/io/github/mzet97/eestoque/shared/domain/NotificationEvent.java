package io.github.mzet97.eestoque.shared.domain;

import org.springframework.modulith.events.Externalized;

/**
 * Contrato do evento de notificação de erro publicado no exchange
 * notification-service (routing key notification-error) quando um fluxo de
 * negócio registra uma falha — mesmo wire format do .NET.
 */
@Externalized("notification-service::notification-error")
public record NotificationEvent(String message, String details) {
}
