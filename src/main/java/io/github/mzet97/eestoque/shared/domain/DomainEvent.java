package io.github.mzet97.eestoque.shared.domain;

import java.util.UUID;

/**
 * Evento de domínio: fato ocorrido no agregado. Implementações concretas
 * são records nos módulos; os componentes do record definem exatamente o
 * payload publicado (wire-format do .NET, camelCase).
 */
public interface DomainEvent {

    UUID aggregateId();
}
