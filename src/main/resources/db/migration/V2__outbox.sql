-- V2__outbox.sql
-- Transactional Outbox (ADR-012). Tabela adicional, não conflita com o schema .NET.

CREATE TABLE IF NOT EXISTS public.outbox_events
(
    id             uuid         NOT NULL,
    event_id       uuid         NOT NULL,
    event_type     varchar(100) NOT NULL,
    exchange       varchar(100) NOT NULL,
    routing_key    varchar(200) NOT NULL,
    payload        text         NOT NULL,
    status         varchar(20)  NOT NULL,
    attempts       integer      NOT NULL DEFAULT 0,
    correlation_id uuid         NULL,
    occurred_at    timestamp    NOT NULL,
    processed_at   timestamp    NULL,
    version        bigint       NULL,
    CONSTRAINT "PK_outbox_events" PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS "IX_outbox_events_status" ON public.outbox_events (status);
