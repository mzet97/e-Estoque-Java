package io.github.mzet97.eestoque.shared.application;

/**
 * Porta para as notificações de erro de negócio publicadas no broker pelo
 * sistema original (NotificationError → exchange notification-service)
 * antes de falhar a operação.
 */
public interface BusinessNotificationPublisher {

    void publishError(String message, String details);
}
