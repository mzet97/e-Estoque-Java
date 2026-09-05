-- V2__event_publication.sql
-- Tabela do Event Publication Registry do Spring Modulith (ADR-012).
-- Substitui a antiga outbox_events (outbox manual removido).
--
-- ATENÇÃO: com globally_quoted_identifiers=true, a entidade JpaEventPublication
-- do Modulith (sem @Table/@Column explícitos) é validada contra identificadores
-- CITADOS: tabela "EVENT_PUBLICATION" e colunas verbatim camelCase das
-- propriedades da entidade ("listenerId", "publicationDate", ...).

CREATE TABLE IF NOT EXISTS "EVENT_PUBLICATION" (
    "id"                   UUID NOT NULL,
    "listenerId"           VARCHAR(512) NOT NULL,
    "eventType"            VARCHAR(512) NOT NULL,
    "serializedEvent"      TEXT NOT NULL,
    "publicationDate"      TIMESTAMP WITH TIME ZONE NOT NULL,
    "completionDate"       TIMESTAMP WITH TIME ZONE,
    "completionAttempts"   INT NOT NULL DEFAULT 0,
    "lastResubmissionDate" TIMESTAMP WITH TIME ZONE NULL,
    "status"               VARCHAR(20) NULL,
    PRIMARY KEY ("id")
);

CREATE INDEX IF NOT EXISTS event_publication_by_completion_date_idx
    ON "EVENT_PUBLICATION" ("completionDate");
