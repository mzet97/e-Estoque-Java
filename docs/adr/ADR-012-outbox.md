# ADR-012 — Eventos RabbitMQ com o Event Publication Registry do Spring Modulith

**Status:** Aceito · **Data:** 2026-09-04 · **Emendado:** 2026-09-05

**Contexto:** .NET publica em RabbitMQ após SaveChanges sem transação (perda possível). Master prompt §17.

**Decisão original (2026-09-04):** outbox manual — tabela `outbox_events` + dispatcher assíncrono próprio.

## Emenda (2026-09-05): registry pronto do Spring Modulith substitui o outbox manual

**Decisão:** Usar o **Event Publication Registry** do Spring Modulith (`spring-modulith-events-jpa` + `spring-modulith-events-amqp` + `spring-modulith-events-jackson`):

- O handler publica o evento de domínio via `ApplicationEventPublisher` (port `DomainEventPublisher`/`BusinessNotificationPublisher` implementados por `ModulithEventPublisher`).
- O registry **persiste a publicação na mesma transação** do agregado (tabela `"EVENT_PUBLICATION"`, Flyway V2) e a marca COMPLETED após o processamento.
- A externalização para o RabbitMQ acontece **somente após o commit**, via `@DomainEvents`/registry + `spring-modulith-events-amqp`; exchange e routing key declarados na anotação `@Externalized("exchange::routing-key")` de cada evento (payload serializado pelo Jackson 3 — `events-jackson`).
- Retry/resubmission: mecanismos nativos do registry (`completion_attempts`, `last_resubmission_date`, `status`).

**Por que emendar:** o dispatcher manual (`polling` + `AFTER_COMMIT` listener + retry/backoff próprios) duplicava funcionalidade que o Modulith já provê testada; a tabela `outbox_events` e o `WireFormat` manual foram removidos. Wire-format publicado permanece idêntico ao .NET (JSON camelCase, exchanges topic duráveis, routing keys dash-case).

**Achados de runtime (ver MIGRATION-DIFFERENCES MD-26/MD-27):** o DDL do V2 precisou usar identificadores citados (`"EVENT_PUBLICATION"` + colunas camelCase) por causa de `globally_quoted_identifiers`; e `spring-modulith-events-jackson` é obrigatório (sem ele não há bean `EventSerializer` e nada é externalizado).

**Consequências:** At-least-once (consumers idempotentes — já eram para retries de rede no .NET); ordenação global não garantida (igual ao .NET); eventos nunca publicados antes do commit; menos código próprio (tabela, dispatcher, retry e wire-format delegados ao Modulith).
