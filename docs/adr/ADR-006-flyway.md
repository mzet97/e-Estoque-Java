# ADR-006 — Flyway

**Status:** Aceito · **Data:** 2026-09-04

**Contexto:** Substituir migrations EF Core (uma única migration 001).
**Decisão:** `V1__baseline.sql` reproduz o schema .NET (tabelas case-sensitive entre aspas, PK_/FK_/IX_ idênticos); `V2__outbox.sql` adiciona outbox; migrations imutáveis após publicadas.
**Consequências:** Bancos criados pelo .NET ou pelo Java ficam intercambiáveis (outbox é aditiva).
