# ADR-012 — Transactional Outbox para eventos RabbitMQ

**Status:** Aceito · **Data:** 2026-09-04

**Contexto:** .NET publica em RabbitMQ após SaveChanges sem transação (perda possível). Master prompt §17.
**Decisão:** Tabela `outbox_events` (id, event_type, exchange, routing_key, payload jsonb, occurred_at, processed_at, status, attempts, correlation_id, event_id) gravada NA MESMA transação do agregado; dispatcher assíncrono (polling + AFTER_COMMIT listener para latência) publica via Spring AMQP com confirmações; retry com backoff exponencial; após N tentativas → status FAILED + log de erro (dead-letter do outbox); idempotência por event_id. Wire-format publicado idêntico ao .NET (JSON camelCase, exchanges topic duráveis, routing keys dash-case).

**Consequências:** At-least-once (consumers idempotentes necessários — já eram para retries de rede no .NET); ordenação global não garantida (igual ao .NET); eventos nunca publicados antes do commit.
