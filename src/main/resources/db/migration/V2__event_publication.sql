-- V2__event_publication.sql
-- Tabela do Event Publication Registry do Spring Modulith (ADR-012).
-- Substitui a antiga outbox_events (outbox manual removido).

CREATE TABLE IF NOT EXISTS event_publication (
    id                   UUID NOT NULL,
    listener_id          VARCHAR(512) NOT NULL,
    event_type           VARCHAR(512) NOT NULL,
    serialized_event     TEXT NOT NULL,
    publication_date     TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date      TIMESTAMP WITH TIME ZONE,
    completion_attempts  INT NOT NULL DEFAULT 0,
    last_resubmission_date TIMESTAMP WITH TIME ZONE NULL,
    status               VARCHAR(20) NULL,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS event_publication_serialized_event_hash_idx
    ON event_publication (serialized_event_hash);
CREATE INDEX IF NOT EXISTS event_publication_by_completion_date_idx
    ON event_publication (completion_date);
